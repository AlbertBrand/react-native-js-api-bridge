import { NativeModules } from 'react-native';

const JSAPI = NativeModules.AndroidJSAPI;

function matchingArgTypes(args, refArgTypes = []) {
  if (args.length != refArgTypes.length) return;

  let match = true;
  for (let i = 0; i < args.length; i++) {
    // TODO more sophisticated argument matching
    if (refArgTypes[i] == 'int' && !Number.isInteger(args[i])) match = false;
  }
  return match;
}

function wrapMethod(objectId = 0, className, methodReflection, prevWrappedFn) {
  return async function () {
    if (!matchingArgTypes(arguments, methodReflection.arguments)) {
      console.log('mismatch params, continue with prev wrapped');
      return prevWrappedFn && prevWrappedFn(...arguments);
    }

    const typedArguments = [];
    for (let i = 0; i < arguments.length; i++) {
      typedArguments.push([methodReflection.arguments[i], arguments[i]]);
    }
    // TODO convert arguments type?
    try {
      // TODO don't bridge if primitive is returned
      const reflection = await JSAPI.methodCall(objectId, className, methodReflection.name, typedArguments);
      return createWrapper(reflection);

    } catch (e) {
      console.error(e);
    }
  };
}

function createWrapper(reflection) {
  const ret = {};
  for (let method of reflection.methods) {
    ret[method.name] = wrapMethod(reflection.objectId, reflection.className, method, ret[method.name]);
  }
  for (let field of reflection.fields) {
    ret[field.name] = field.value;
  }
  return ret;
}

async function createBridge(className) {
  try {
    const reflection = await JSAPI.reflect(className);
    return createWrapper(reflection);

  } catch (e) {
    console.error(e);
  }
}

module.exports = {
  createBridge,
  context: '*context'
};
