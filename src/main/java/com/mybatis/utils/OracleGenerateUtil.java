package com.mybatis.utils;

import com.intellij.database.model.DasObject;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.ObjectKind;
import com.intellij.util.containers.JBIterable;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

public class OracleGenerateUtil {

    /**
     * Find sequence name optional.
     *
     * @param dasTable  the das table
     * @param tableName the table name
     * @return the optional
     */
    public static Optional<String> findSequenceName(DasTable dasTable, String tableName) {
        String foundSequenceName = null;
        if (dasTable != null) {
            DasObject schema = dasTable.getDasParent();
            if (schema == null) {
                return Optional.empty();
            }
            PriorityQueue<String> sequenceQueue = new PriorityQueue<>(Comparator.comparing(String::length, Comparator.reverseOrder()));

            JBIterable<? extends DasObject> sequences = schema.getDasChildren(ObjectKind.SEQUENCE);
            for (DasObject sequence : sequences) {
                String sequenceName = sequence.getName();
                if (sequenceName.contains(tableName)) {
                    sequenceQueue.add(sequenceName);
                }
            }
            if (sequenceQueue.size() > 0) {
                foundSequenceName = sequenceQueue.peek();
            }

        }
        return Optional.ofNullable(foundSequenceName);
    }

}
