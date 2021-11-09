CREATE TABLE product
(
    internal_code INT NOT NULL,
    name VARCHAR NOT NULL,

    CONSTRAINT product_pk PRIMARY KEY (internal_code)
);

CREATE TABLE organization
(
    itn INT NOT NULL,
    name VARCHAR NOT NULL,
    payment_account VARCHAR NOT NULL,

    CONSTRAINT organization_pk PRIMARY KEY (itn)
);

CREATE TABLE invoice --накладная
(
    id   SERIAL  NOT NULL,
    date DATE NOT NULL,
    sender_org_itn INT NOT NULL REFERENCES organization (itn) ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT invoice_pk PRIMARY KEY (id)
);

CREATE TABLE invoice_item
(
    invoice_id INT NOT NULL REFERENCES invoice (id) ON UPDATE CASCADE ON DELETE CASCADE,
    product_code INT NOT NULL REFERENCES product (internal_code) ON UPDATE CASCADE ON DELETE CASCADE,
    price INT NOT NULL,
    quantity INT NOT NULL,

    CONSTRAINT invoice_item_pk PRIMARY KEY (invoice_id, product_code)
);