package celtech.roboxbase.configuration.fileRepresentation;

import celtech.roboxbase.BaseLookup;

public enum SupportType {
    /**
     * Use material 1 as support. Implies all objects printed with material 2.
     */
    MATERIAL_1("supportType.material1", 0),
    /**
     * Use material 2 as support. Implies all objects printed with material 1.
     */
    MATERIAL_2("supportType.material2", 1),
    /**
     * Use the material options specified in the profile.
     */
    AS_PROFILE("supportType.profile", 0);

    private final String description;
    private final int extruderNumber;

    SupportType(String description, int extruderNumber) {
        this.description = BaseLookup.i18n(description);
        this.extruderNumber = extruderNumber;
    }

    @Override
    public String toString() {
        return description;
    }

    public int getExtruderNumber() {
        return extruderNumber;
    }
}
