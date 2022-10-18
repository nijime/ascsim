import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FlexResultSet {
    private ArrayList<Field> fields;
    private ResultSet resultSet;

    public FlexResultSet(ResultSet resultSet, ArrayList<Field> fields) {
        this.resultSet = resultSet;
        this.fields = fields;
    }

    public ResultSet getAsNormal() {
        return resultSet;
    }

    public boolean next() throws SQLException {
        return resultSet.next();
    }


}
