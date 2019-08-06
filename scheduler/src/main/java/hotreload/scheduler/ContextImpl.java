package hotreload.scheduler;

import hotreload.api.Context;

class ContextImpl implements Context {

    @Override
    public int activeAppCount() {
        return Engine.getInstance().getAppSize();
    }

}