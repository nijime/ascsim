public class Field {
    private enum Type {
        INT(0), DECIMAL(1), BIT(2), VARCHAR(3);

        private int val;

        Type(int val) {
            this.val = val;
        }

        public int asInt() {
            return val;
        }
    }

    private String fieldName;
    private Type fieldType;

    public Field(String name, Type fieldType) {
        this.fieldName = name;
        this.fieldType = fieldType;
    }

    public Field(String name, String fieldType) {
        switch (fieldType.toLowerCase()) {
            case "int":
                this.fieldName = name;
                this.fieldType = Type.INT;
                break;
            case "decimal":
                this.fieldName = name;
                this.fieldType = Type.DECIMAL;
                break;
            case "bit":
                this.fieldName = name;
                this.fieldType = Type.BIT;
                break;
            default:
                this.fieldName = name;
                this.fieldType = Type.VARCHAR;
                break;
        }
    }

    public String getName() {
        return fieldName;
    }

    public Type getType() {
        return fieldType;
    }

    public int getTypeAsInt() {
        return fieldType.asInt();
    }

    public void print() {
        System.out.println("Field[" + getName() + " - " + getType().toString() + "]");
    }
}