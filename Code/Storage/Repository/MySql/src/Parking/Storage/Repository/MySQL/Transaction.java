/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parking.Storage.Repository.MySQL;

import Parking.Base.Debugger;
import Parking.Core.EntityObject;
import Parking.Storage.TransactionParameters;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Transaction {

    private Boolean canCommit = false;
    private Boolean isPrepared = false;
    private MySQLRepository repository;

    private Connection connection;
    private TransactionParameters transactionParameters;
    private ArrayList<PreparedStatement> statements;

    public Transaction(MySQLRepository repository, TransactionParameters transactionParameters) {
        this.repository = repository;
        this.statements = new ArrayList<>();
        this.transactionParameters = transactionParameters;
    }

    public void Prepare() {
        try {
            if (this.connection == null) {
                this.connection = this.repository.GetConnection();
            }

            this.connection.setAutoCommit(false);
            this.isPrepared = true;
        } catch (SQLException sqlException) {
            Debugger.Log("Exception in Transaction.Prepare(): %s", sqlException.getMessage());
        }
    }

    public void Save(EntityObject entityObject) throws IllegalStateException {
        if (!this.isPrepared) {
            throw new IllegalStateException("The Transaction MUST be prepared before Save() is called!");
        }

        Boolean useUpdate = false;

        Class entityClass = entityObject.getClass();

        String tableName = Utils.GetTableName(entityClass);

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(String.format("SELECT COUNT(ID) FROM %s WHERE ID='%s'", tableName, entityObject.ID));

            if (resultSet.first()) {
                if (resultSet.getInt(1) > 0) {
                    useUpdate = true;
                }
            }
        } catch (SQLException exception) {
            System.out.println(exception);
        }

        List<FieldValue> fieldValues = Utils.GetFieldValues(entityObject, entityClass);

        if (useUpdate) {
            if (this.PrepareUpdateStatement(entityObject, tableName, fieldValues)) {
                this.canCommit = true;
            } else {
                System.err.println("Failed to Update Object.");
            }
        } else {
            if (this.PrepareInsertStatement(tableName, fieldValues)) {
                this.canCommit = true;
            } else {
                System.err.println("Failed to save Object.");
            }
        }
    }

    public Boolean Commit() {
        Boolean isSuccessful = false;

        if ((this.isPrepared) && (this.canCommit)) {
            try {
                this.connection.commit();
                isSuccessful = true;
            } catch (SQLException sqlException) {
                try {
                    System.err.print("Transaction is being rolled back");
                    this.connection.rollback();
                } catch (SQLException rollbackException) {
                    System.err.println(rollbackException);
                }
            }
        }

        return isSuccessful;
    }

    private Boolean PrepareUpdateStatement(EntityObject entityObject, String tableName, List<FieldValue> fieldValues) {
        Boolean isSuccessful = false;

        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append(String.format("Update %s SET ", tableName));

        for (FieldValue fieldValue : fieldValues) {
            if ((Utils.IsEligable(fieldValue.getKey())) && (!fieldValue.getIsCollection())) {
                commandBuilder.append(String.format("%s = ?, ", fieldValue.getKey()));
            }
        }

        commandBuilder.append(String.format("ModifiedOn=? WHERE ID='%s'", entityObject.ID));

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(commandBuilder.toString());

            Integer counter = 1;

            for (FieldValue fieldValue : fieldValues) {
                if (Utils.IsEligable(fieldValue.getKey())) {
                    if (!fieldValue.getIsCollection()) {
                        if (fieldValue.getIsReferencedObject()) {
                            String referencedID = "";

                            if (fieldValue.getValue() != null) {
                                EntityObject referencedObject = (EntityObject) fieldValue.getValue();

                                referencedID = referencedObject.ID;
                            }

                            preparedStatement.setString(counter, referencedID);
                        } else {
                            preparedStatement.setObject(counter, fieldValue.getValue());
                        }

                        counter++;
                    } else {
                        if (fieldValue.getValue() != null) {
                            if (Collection.class.isAssignableFrom(fieldValue.getValue().getClass())) {
                                for (Object item : (Collection) fieldValue.getValue()) {
                                    if (EntityObject.class.isAssignableFrom(item.getClass())) {
                                        if (this.transactionParameters.IsSavingCascade) {
                                            this.repository.Save((EntityObject) item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            preparedStatement.setTimestamp(counter, new java.sql.Timestamp(new java.util.Date().getTime()));

            preparedStatement.executeUpdate();
            isSuccessful = true;

            this.statements.add(preparedStatement);
        } catch (SQLException exception) {
            exception.printStackTrace();
            //System.out.println("Parking.Storage.Repository.MySQL.Transactions.Transaction.PrepareUpdateStatement()");
        }

        return isSuccessful;
    }

    private Boolean PrepareInsertStatement(String tableName, List<FieldValue> fieldValues) {
        Boolean isSuccessful = false;

        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append(String.format("INSERT INTO %s (", tableName));
        String values = "";

        for (FieldValue fieldValue : fieldValues) {
            if (!fieldValue.getIsCollection()) {
                commandBuilder.append(String.format("%s, ", fieldValue.getKey()));
                values += "?, ";
            }
        }

        commandBuilder.deleteCharAt(commandBuilder.length() - 2);
        values = values.substring(0, values.length() - 2);

        commandBuilder.append(String.format(") VALUES (%s)", values));

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(commandBuilder.toString());

            Integer counter = 1;

            for (FieldValue fieldValue : fieldValues) {
                if (!fieldValue.getIsCollection()) {
                    if (fieldValue.getIsReferencedObject()) {
                        String referencedID = "";

                        if (fieldValue != null) {
                            EntityObject referencedObject = (EntityObject) fieldValue.getValue();

                            referencedID = referencedObject.ID;
                        }

                        preparedStatement.setString(counter, referencedID);
                    } else {
                        preparedStatement.setObject(counter, fieldValue.getValue());
                    }

                    counter++;
                } else {
                    if (fieldValue.getValue() != null) {
                        if (Collection.class.isAssignableFrom(fieldValue.getValue().getClass())) {
                            for (Object item : (Collection) fieldValue.getValue()) {
                                if (EntityObject.class.isAssignableFrom(item.getClass())) {
                                    if (this.transactionParameters.IsSavingCascade) {
                                        this.repository.Save((EntityObject) item);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            preparedStatement.executeUpdate();
            isSuccessful = true;

            this.statements.add(preparedStatement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return isSuccessful;
    }
}
