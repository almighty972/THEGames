package com.thegames.therightnumber.interfaces;

import com.thegames.therightnumber.popups.Popup;
import com.thegames.therightnumber.uigame.PopupAction;

/**
 * Created by gyljean-lambert on 25/05/2014.
 */
public interface PopupDialogListener {

    <T extends Popup> void onClickPopupButton(T popup, PopupAction popupAction);
    void onCountdownFinished();
}
