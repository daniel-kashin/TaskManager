package com.danielkashin.taskorganiser.data_layer.entities.local.data;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import static com.danielkashin.taskorganiser.data_layer.contracts.local.base.IdContract.COLUMN_NAME_ID;
import static com.danielkashin.taskorganiser.data_layer.contracts.local.data_tables.TagContract.COLUMN_NAME_NAME;
import static com.danielkashin.taskorganiser.data_layer.contracts.local.data_tables.TagContract.TABLE_NAME;


@StorIOSQLiteType(table = TABLE_NAME)
public class Tag {

  @StorIOSQLiteColumn(name = COLUMN_NAME_ID, key = true)
  Long id;

  @StorIOSQLiteColumn(name = COLUMN_NAME_NAME)
  String name;


  Tag() {
  }

  public Tag(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

}
