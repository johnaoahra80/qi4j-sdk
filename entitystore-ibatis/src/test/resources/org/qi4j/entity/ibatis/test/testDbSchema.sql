CREATE TABLE SA.TEST_ENTITY
(
  ID                  VARCHAR  (50)    NOT NULL,
  NAME                VARCHAR  (40)   NOT NULL,
  UNSET_NAME          VARCHAR  (40)   ,
  SOME_VALUE          VARCHAR  (40)   ,
  OTHER_VALUE         INTEGER,
  ASSOC_ID            VARCHAR  (50) ,
  UNSET_ASSOC_ID      VARCHAR  (50) ,
  PRIMARY KEY (ID)
);
