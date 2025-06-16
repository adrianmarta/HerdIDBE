package com.example.farmerapp.models;

public enum AnimalSpecies {

    ASSAF("Assaf"),
    AWASSI("Awassi"),
    BERGAMASCA("Bergamasca"),
    BERRICHON_DU_CHER("Berrichon du Cher"),
    BLACKNOSE_VALAIS("Blacknose Valais"),
    BLANC_DU_MASSIF_CENTRAL("Blanc du Massif Central"),
    BLUE_FACE_LEICESTER("Blue face Leicester"),
    BRAUNES_BERGSCHAF("Braunes Bergschaf"),
    CAMERUN("Camerun"),
    CARACUL("Caracul"),
    CHAROLLAIS("Charollais"),
    COBURGER_FUCHSSCHAF("Coburger Fuchsschaf"),
    CRUCE_ASSAF("Cruce Assaf"),
    DORPER("Dorper"),
    FRIZA("Friza"),
    GERMANA_CU_CAPUL_NEGRU("Germana cu Capul Negru"),
    GOTLAND_PELT("Gotland Pelt"),
    HAMPSHIRE("Hampshire"),
    ILE_DE_FRANCE("Ile de France"),
    JAKOBSCHAF("Jakobschaf"),
    JURASCHAF("Juraschaf"),
    KARNTNER_BRILLENSCHAF("Karntner Brillenschaf"),
    LACAUNE("Lacaune"),
    MERINOLANDSCHAF("MERINOLANDSCHAF"),
    MERINOS("Merinos"),
    MERINOS_AUSTRALIAN("Merinos Australian"),
    MERINOS_DE_CLUJ("Merinos de Cluj"),
    MERINOS_DE_PALAS("Merinos de Palas"),
    MERINOS_DE_STAVROPOL("Merinos de Stavropol"),
    MERINOS_DE_SUSENI("Merinos de Suseni"),
    MERINOS_DE_TRANSILVANIA("Merinos de Transilvania"),
    METIS("Metis"),
    OAIA_CAP_NEGRU("Oaia Cap Negru"),
    OAIA_CAP_NEGRU_DE_TELEORMAN("Oaia Cap Negru de Teleorman"),
    OUESSANT("Ouessant"),
    POLWARTH("Polwarth"),
    PROLIFICA_PALAS("Prolifica Palas"),
    RACKA("Racka"),
    RASA_DE_CARNE_PALAS("Rasa de Carne Palas"),
    RASA_DE_LAPTE_PALAS("Rasa de Lapte Palas"),
    ROMANOV("Romanov"),
    ROMNEY("Romney"),
    ROUGE_DE_L_OUEST("ROUGE DE L OUEST"),
    SARDA("Sarda"),
    SHROPSHIRE("Shropshire"),
    SUFFOLK("Suffolk"),
    TEXEL("Texel"),
    TIGAIE("Tigaie"),
    TIGAIE_VARIETATEA_RUGINIE("Tigaie Varietatea Ruginie"),
    TURCANA("Turcana"),
    TURCANA_VARIETATEA_RATCA("Turcana Varietatea Ratca"),
    VENDEEN("Vendeen"),
    ZWARTBLES("Zwartbles");

    private final String displayName;

    AnimalSpecies(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}