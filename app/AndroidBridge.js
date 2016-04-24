import { NativeModules } from 'react-native';

const JSAPI = NativeModules.AndroidJSAPI;

function matchingArgTypes(args, refArgTypes) {
  if (args.length != refArgTypes.length) return;
  let match = true;
  for (let i = 0; i < arguments.length; i++) {
    // TODO
    if (refArgTypes[i] == 'int' && !Number.isInteger(arguments[i])) match = false;
  }
  return true;
}

function wrapRefProp(className, reflect, prevWrapped) {
  switch (reflect.type) {
    case 'staticMethod':
      return async function () {
        if (!matchingArgTypes(arguments, reflect.arguments)) {
          console.log('mismatch params, continue with prev wrapped');
          return prevWrapped && prevWrapped(...arguments);
        }

        const typedArguments = [];
        for (let i = 0; i < arguments.length; i++) {
          typedArguments.push([reflect.arguments[i], arguments[i]]);
        }
        // TODO convert arguments type?
        try {
          // TODO bridge if instance returned
          return JSAPI.staticMethodCall(className, reflect.name, typedArguments);
        } catch (e) {
          console.error(e);
        }
      };
    case 'staticField':
      return reflect.value;
    default:
      console.error('unknown type')
  }
}

async function createBridge(className) {
  try {
    const refProps = await JSAPI.reflect(className);

    const ret = {};
    for (let refProp of refProps) {
      ret[refProp.name] = wrapRefProp(className, refProp, ret[refProp.name]);
    }
    return ret;

  } catch (e) {
    console.error(e);
  }
}

module.exports = {
  createBridge,
  context: '*context'
};
