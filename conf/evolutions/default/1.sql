# --- !Ups

create table category
(
  "id" integer not null primary key autoincrement,
  "name" varchar not null
);

create table product
(
  "id" integer not null primary key autoincrement,
  "name" varchar not null,
  "description" text not null,
  category int not null,
  foreign key(category) references category(id)
);

CREATE TABLE basket_ids
(
  "basket_id" INTEGER NOT NULL PRIMARY KEY autoincrement,
  "user"      INTEGER NOT NULL
);

CREATE TABLE basket_product
(
  "basket"  INTEGER NOT NULL PRIMARY KEY,
  "product" INTEGER NOT NULL,
  "amount"  INT DEFAULT 1 NOT NULL,
  FOREIGN KEY (basket) REFERENCES basket_ids(basket_id),
  FOREIGN KEY (product) REFERENCES product(id)
);

CREATE TABLE keyword
(
  "keyword_id" INTEGER     NOT NULL PRIMARY KEY autoincrement,
  "value"      VARCHAR(30) NOT NULL
);

CREATE TABLE review
(
  "product" INTEGER NOT NULL PRIMARY KEY autoincrement,
  "comment" TEXT    NOT NULL,
  FOREIGN KEY(product) REFERENCES product(id)
);

CREATE TABLE "order"
(
  "order_id" INTEGER NOT NULL PRIMARY KEY autoincrement,
  "basket"   INTEGER NOT NULL,
  "date"     DATE    NOT NULL,
  "address"  INTEGER NOT NULL,
  FOREIGN KEY (basket) REFERENCES basket_ids(basket_id)
);

CREATE TABLE payment
(
  "payment_id" INTEGER        NOT NULL PRIMARY KEY autoincrement,
  "basket"     INTEGER        NOT NULL,
  "value($)" DECIMAL(19, 4) NOT NULL,
  "date"       DATE           NOT NULL,
  FOREIGN KEY (basket) REFERENCES basket_ids(basket_id)
);

CREATE TABLE product_type
(
  type_id INTEGER     NOT NULL PRIMARY KEY autoincrement,
  name    VARCHAR(25) NOT NULL
);


# --- !Downs

DROP TABLE "product";
DROP TABLE "category";
DROP TABLE "basket_ids";
DROP TABLE "basket_product";
DROP TABLE "keyword";
DROP TABLE "review";
DROP TABLE "order";
DROP TABLE "payment";
DROP TABLE "product_type";

