package com.hsbc.greenpacket.util.actions;

import java.util.HashMap;
import java.util.Map;

public class HSBCURLResolverUI15 {


    /**
     * Holds the available action strategies.
     */

    private static class StrategiesHolder {

    }


    private final static Map<String, HSBCURLAction> actionStrategies = new HashMap<String, HSBCURLAction>();
    static {
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.POPUP_BUBBLE, new PopUpBubbleAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.GET_DEVICE_TYPE, new GetDeviceTypeAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.GET_ENCRYPTED_DEVICE_ID, new GetEncryptedDeviceIdAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.PLAY_SOUND_BY_TYPE, new PlaySoundByTypeAction());
        
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.PROXY, new ProxyAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.PAGE_TRANSITION, new PageTransitionAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.BACK_TO_APP, new BackToAppAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.GETTER, new GetterAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.SETTER, new SetterAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.PROXYJSON, new ProxyJsonAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.GET_LOCALE, new GetLocaleAction());
        
		//Post processing end hook
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.REGISTER_GESTURE_LISTENER, new RegisterGestureListenerAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.UNREGISTER_GESTURE_LISTENER, new UnregisterGestureListenerAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.REGISTER_LONG_PRESS_LISTENER, new RegisterLongPressListenerAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.UNREGISTER_LONG_PRESS_LISTENER, new UnregisterLongPressListenerAction());
        //Tracy Wang [16 Dec 2013]
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.REGISTER_SHAKE_LISTENER, new RegisterShakeListenerAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.UNREGISTER_SHAKE_LISTENER, new UnregisterShakeListenerAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.VALIDATE_SECURE_TOKEN, new ValidateSecureTokenAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.VOLATILEGETTER, new VolatileGetterAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.VOLATILESETTER, new VolatileSetterAction());
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.CLOSE_WEBVIEW, new CloseWebViewAction());
        
        //Tracy Wang [13 Jan 2015]
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.GO_TO_SCA, new GoToSCAAction());
        
        
        
        
        
        
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.SOUNDWAVE_TO_NATIVE, new SoundWaveToNative()) ;
        HSBCURLResolverUI15.actionStrategies.put(HookConstants.SHOW_ABOUT_NATIVE, new ShowAboutNative()) ;
    }

    public static HSBCURLAction resolve(final String url) {
        return HSBCURLResolver.resolve(url, HSBCURLResolverUI15.actionStrategies);
    }

    /**
     * Add resolveByFunction to get the action instance to execute the
     * dataProcess method
     * 
     * @author York
     * @param function
     * @return
     */
    public static IHsbcUrlAction resolveByFunction(final String function) {
        HSBCURLAction action = HSBCURLResolverUI15.actionStrategies.get(function);
        if (action instanceof IHsbcUrlAction) {
            return (IHsbcUrlAction) action;
        }
        return null;
    }
}
