package frontend;

import backend.Figure;

public class CheckBoxState {
    private boolean commonShadowStatus;
    private boolean commonGradientStatus;
    private boolean commonBeveledStatus;


    public CheckBoxState() {
        resetState();
    }

    public void resetState() {
        commonShadowStatus = true;
        commonGradientStatus = true;
        commonBeveledStatus = true;
    }

    public void updateState(FrontFigure<? extends Figure> figure) {
        commonShadowStatus &= figure.getShadowStatus();
        commonGradientStatus &= figure.getGradientStatus();
        commonBeveledStatus &= figure.getBeveledStatus();
    }

    public boolean getCommonShadowStatus() {
        return commonShadowStatus;
    }

    public boolean getCommonGradientStatus() {
        return commonGradientStatus;
    }

    public boolean getCommonBeveledStatus() {
        return commonBeveledStatus;
    }

}
