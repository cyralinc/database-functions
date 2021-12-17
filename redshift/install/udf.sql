create schema if not exists ${SCHEMA};
grant usage on schema ${SCHEMA} to PUBLIC;

create or replace function ${SCHEMA}.${FUNC_NAME}(data anyelement, mask varchar)
  returns anyelement
stable
as $$
  import decimal
  import re
  import string
  from datetime import datetime
  import random
  import json

  def generate_random_char(current_char):
    if re.search("[A-Z]", current_char):
        return random.choice(string.ascii_uppercase)

    if re.search("[a-z]", current_char):
        return random.choice(string.ascii_lowercase)

    if re.search("[0-9]", current_char):
        return str(random.randrange(10))

    return current_char

  def preserve_mask(field_value, mask_param):
      # Boolean testing needs to come before int since boolean is a subclass of int
      if isinstance(field_value, bool):
        return random.choice((True,False))

      if isinstance(field_value, basestring):
        return_string = ""
        for element in field_value:
          return_string += generate_random_char(element)
        return return_string

      if isinstance(field_value, (int,float,decimal.Decimal)):
        return type(field_value)(random.randrange(int(field_value)))

      # We have no idea what to do so we raise an exception
      raise Exception("Unknown field type : " + type(field_value))
  
  def constant_mask(field_value, mask_param):
      # Check if the optional mask_param is set and that both are the same type
      if mask_param is not None:
        if isinstance(field_value, datetime):
          raise Exception("TODO :: Handle Constant Date")
        else:
          return type(field_value)(mask_param)
        
      if isinstance(field_value, basestring):
        return "REDACTED"

      # Boolean testing needs to come before int since boolean is a subclass of int
      if isinstance(field_value, bool):
        return False

      if isinstance(field_value, (int,float,decimal.Decimal)):
        return type(field_value)('0')
      
      if isinstance(field_value, datetime):
        return datetime.strptime('Oct 1 1971  8:00AM', '%b %d %Y %I:%M%p')

      # We have no idea what to do so we raise an exception
      raise Exception("Unknown field type : " + type(field_value))
                                  
  def safe_test(field_value, mask_type, mask_param=None): 
    # We can return NULL right away if there's a NULL mask
    if(mask_type == "null_mask"):
      return None

    # First we check for a NULL field value
    if field_value is None:
      return field_value
      
    # Check for a preserve mask type to return
    if(mask_type == "mask"):
      return preserve_mask(field_value=field_value, mask_param=mask_param)

    # Check for a constant mask type to return
    if(mask_type == "constant_mask"):
      return constant_mask(field_value=field_value, mask_param=mask_param[0])

    # No idea what to do so we'll return unmasked
    raise Exception(type(field_value))

  # Let's be safe and run everything inside a try
  # Failure returns the data type unmasked
  try:
    maskObj = json.loads(mask)
    if 'maskFunction' not in maskObj:
      return data
    if 'args' not in maskObj:
      maskObj['args'] = []


    return safe_test(field_value=data, mask_type=maskObj['maskFunction'], mask_param=maskObj['args'])
  # With any failure we'll just return the data unmasked
  except:
    return data
$$ language plpythonu;

grant execute on function ${SCHEMA}.${FUNC_NAME}(data anyelement, mask varchar) to PUBLIC;
comment on function ${SCHEMA}.${FUNC_NAME}(data anyelement, mask varchar) is '${SCHEMA}.${FUNC_NAME}\n${VERSION}\n${TIMESTAMP}';
