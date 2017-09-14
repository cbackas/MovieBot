package cback;

public enum MovieRoles {
    STAFF("STAFF", 257995763399917569l),
    ADMIN("ADMIN", 256249078596370433l),
    NETWORKMOD("NETWORKMOD", 256878689503936513l),
    MOD("MOD", 256249088830472193l),
    MOVIENIGHT("MOVIENIGHT", 226443478664609792l);

    public String name;
    public long id;

    MovieRoles(String name, long id) {
        this.name = name;
        this.id = id;
    }
}