package cback;

public enum Rules {
    one("1", "Rule One | Civil Discussion", Util.getRule(263191515084488705l)),
    two("2", "Rule Two | Spam", Util.getRule(263191544411062272l)),
    three("3", "Rule Three | Self-Promotion", Util.getRule(263191574702325761l)),
    four("4", "Rule Four | Spoilers", Util.getRule(263192143617720330l)),
    five("5", "Rule Five | NSFW Content", Util.getRule(263192182175825920l)),
    six("6", "Rule Six | Bots and Exploits", Util.getRule(263192217970016257l)),
    other("other", "Other", Util.getRule(263192257988001792l));

    public String number;
    public String title;
    public String fullRule;

    Rules(String number, String title, String fullRule) {
        this.title = title;
        this.number = number;
        this.fullRule = fullRule;
    }

    public static Rules getRule(String number) {
        for (Rules rule : values()) {
            if (rule.number.equalsIgnoreCase(number)) {
                return rule;
            }
        }
        return null;
    }
}