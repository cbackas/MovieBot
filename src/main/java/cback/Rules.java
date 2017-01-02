package cback;

public enum Rules {
    one("1", Util.getRule("263191515084488705")),
    two("2", Util.getRule("263191544411062272")),
    three("3", Util.getRule("263191574702325761")),
    four("4", Util.getRule("263192143617720330")),
    five("5", Util.getRule("263192182175825920")),
    six("6", Util.getRule("263192217970016257")),
    other("other", Util.getRule("263192257988001792"));

    public String number;
    public String fullRule;

    Rules(String number, String fullRule) {
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