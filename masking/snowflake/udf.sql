CREATE OR REPLACE FUNCTION %s.CyralMask(mask_spec array, columns array)
  RETURNS TABLE (masked_columns array)
  LANGUAGE JAVASCRIPT
  AS '{
      initialize: function(argumentInfo, context) {
        // This function walks through all of the possible validations
        // that we need before processing the data
        this.validateInputs = function(mask_type, data) {
          // First we need to make sure we were given "arrays"
          // -- arrays are converted to objects when sent in
          if(typeof(mask_type) != "object" || typeof(data) != "object") {
            return false;
          }
          // Next check to see if there is data in both arrays
          if(mask_type.length < 1 || data.length < 1) {
            return false;
          }
          // We need to make sure that both provided arrays are of equal length
          if(mask_type.length != data.length) {
            return false;
          }
          // Everything has passed so far so let us assume all is ok
          return true;
        };
        // Function will perform the needed steps to take an initial value and then
        // returns a similar data type formatted with random characters of similar length
        // TODO Decide what to do with utf8/utf16 characters that are not upper-case or lower-case.
        this.preserve_format_mask = function(data) {
          // Check for a string
          if(typeof(data) == "string") {
              var result = ""
              for (var i = 0; i < data.length; i++) {
                var c = data.charAt(i);
                if (c >= "a" && c <= "z") {
                  result += String.fromCharCode(97 + Math.floor(Math.random() * 26));
                } else if (c >= "A" && c <= "Z") {
                  result += String.fromCharCode(65 + Math.floor(Math.random() * 26));
                } else if (c >= "0" && c  <= "9") {
                  result += Math.floor(Math.random() * 10);
                } else {
                  result += c;
                }
              }
              return result;
          }
          // Check for number
          if(typeof(data) == "number") {
            var len = data.toString().length;
            var str = [...Array(len)].map(_=>(Math.random()*10|0).toString(10)).join``;
            return parseInt(str);
          }
          // We do not support the data type so we return it unmasked
          return data; // TODO: Support other data types.
        }
        // This function will determine which masking type will be performed on the data
        // and then return the value based upon the masking type
        this.maskFunction = function(mask_type, data) {
          const mask = JSON.parse(mask_type);
        
          switch(mask["maskFuncName"]) {
            case "null_mask":
              return undefined;
            case "constant_mask":
              if (mask["maskFuncArgs"] == undefined || mask["maskFuncArgs"].length != 1) {
                return data;
              }
              return mask["maskFuncArgs"][0];
            case "mask":
              return this.preserve_format_mask(data);
            default:
              return data;
          };
        };
      },
      processRow: function f(row, rowWriter, context){
        if(this.validateInputs(row.MASK_SPEC, row.COLUMNS)) {
          for (var i = 0; i < row.MASK_SPEC.length; i++) {
            row.COLUMNS[i] = this.maskFunction(row.MASK_SPEC[i], row.COLUMNS[i]);
          }
          rowWriter.writeRow({MASKED_COLUMNS: row.COLUMNS});
        } else {
          rowWriter.writeRow({MASKED_COLUMNS: row.COLUMNS}); // TODO : try returning a SQL Error instead
        };
      }}';
