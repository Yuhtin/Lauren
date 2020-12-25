package com.yuhtin.lauren.sql.provider;

import com.google.inject.Inject;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.sql.provider.document.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseProvider {

    @Inject private SQLConnection sqlConnection;

    public List<Document> queryMany(String query) {

        List<Document> documents = new ArrayList();
        try (PreparedStatement statement = sqlConnection.findConnection().prepareStatement(query)) {

            try (ResultSet resultSet = statement.executeQuery()) {

                ResultSetMetaData resultMetaData = resultSet.getMetaData();
                while (resultSet.next()) {

                    Document document = new Document();
                    for (int index = 1; index <= resultMetaData.getColumnCount(); index++) {

                        String name = resultMetaData.getColumnName(index);
                        document.insert(name, resultSet.getObject(index));

                    }

                    documents.add(document);

                }

            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return documents;

    }

    public Document query(String query, Object... values) {
        try (PreparedStatement statement = sqlConnection.findConnection().prepareStatement(query)) {

            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                ResultSetMetaData resultMetaData = resultSet.getMetaData();
                while (resultSet.next()) {
                    Document document = new Document();
                    for (int index = 1; index <= resultMetaData.getColumnCount(); index++) {

                        String name = resultMetaData.getColumnName(index);
                        document.insert(name, resultSet.getObject(index));

                    }

                    return document;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Document();
    }

    public void update(String query, Object... values) {
        try (PreparedStatement statement = sqlConnection.findConnection().prepareStatement(query)) {
            for (int index = 0; index < values.length; index++) {

                Object value = values[index];
                int parameterIndex = index + 1;

                if (value instanceof Boolean) statement.setObject(parameterIndex, ((boolean) value) ? 1 : 0);
                else statement.setObject(parameterIndex, value);

            }

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
