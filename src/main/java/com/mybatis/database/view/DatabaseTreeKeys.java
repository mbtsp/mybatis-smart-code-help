package com.mybatis.database.view;

import com.intellij.openapi.util.Key;

public class DatabaseTreeKeys {
    public static final Key<DatabaseStructure.DbRootGroup> MYBATIS_ROOT_GROUP = Key.create("MYBATIS_ROOT_GROUP");
//    public static final Key<Map<DatabaseStructure.DbGroup, Map<String, DatabaseStructure.DbGroup>>> MYBATIS_DATABASE_GROUPS =  KeyWithDefaultValue.create("MYBATIS_DATABASE_GROUPS", () -> {
//        return ConcurrentFactoryMap.createWeakMap((group) -> {
//            return ConcurrentFactoryMap.create((name) -> {
//                return new DatabaseStructure.DbGroup(name, group,null);
//            }, CollectionFactory::createConcurrentWeakValueMap);
//        });
//    });

}
