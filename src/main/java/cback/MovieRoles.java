package cback;

public enum MovieRoles {
    STAFF("STAFF", 257995763399917569L),
    ADMIN("ADMIN", 256249078596370433L),
    NETWORKMOD("NETWORKMOD", 256878689503936513L),
    MOD("MOD", 256249088830472193L),
    MOVIENIGHT("MOVIENIGHT", 226443478664609792L);

    public String name;
    public long id;

    MovieRoles(String name, long id) {
        this.name = name;
        this.id = id;
    }
}