package org.springframework.boot.autoconfigure.hateoas;

public class HateoasProperties {

    private boolean useHalAsDefaultJsonMediaType = false;

    public HateoasProperties() {
    }

    public boolean getUseHalAsDefaultJsonMediaType() {
        return useHalAsDefaultJsonMediaType;
    }

    public void setUseHalAsDefaultJsonMediaType(boolean useHalAsDefaultJsonMediaType) {
        this.useHalAsDefaultJsonMediaType = useHalAsDefaultJsonMediaType;
    }
}

