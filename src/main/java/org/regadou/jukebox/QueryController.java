package org.regadou.jukebox;

import io.agroal.api.AgroalDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/query")
public class QueryController {

    @Inject
    AgroalDataSource dataSource;
    private Connection connection;
    private Statement statement;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> tables() throws SQLException {
        return getRows(getConnection().getMetaData().getTables(null,null,null,null))
                .stream().map(row -> {
                    Object name = row.get("TABLE_NAME");
                    if (name == null || !"TABLE".equals(row.get("TABLE_TYPE")))
                        return null;
                    return name.toString();
                }).filter(name -> name != null).collect(Collectors.toList());
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String,Object>> query(String query) throws SQLException {
        getConnection();
        return getRows(statement.executeQuery(query));
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
            try {
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } catch (Exception e) {
                statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
        }
        return connection;
    }

    private List<Map<String,Object>> getRows(ResultSet rs) throws SQLException {
        List<Map<String,Object>> recs = new ArrayList<>();
        while (rs.next())
            recs.add(getRow(rs, null));
        return recs;
    }

    private Map<String,Object> getRow(ResultSet rs, String table) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int nc = meta.getColumnCount();
        Map<String,Object> row = new LinkedHashMap<>();
        for (int c = 1; c <= nc; c++) {
            String col = meta.getColumnName(c);
            Object val = rs.getObject(c);
            row.put(col, val);
        }
        return row;
    }
}
