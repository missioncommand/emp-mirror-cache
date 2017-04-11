package mil.emp3.mirrorcache;

public enum Priority {

    HIGH    (1),
    MEDIUM (50),
    LOW   (100),
    ;
    
    
    final private int value;
    
    private Priority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    static public Priority fromValue(int value) {
        for (Priority p: Priority.values()) {
            if (p.value == value) {
                return p;
            }
        }
        throw new IllegalArgumentException("" + value);
    }
}
