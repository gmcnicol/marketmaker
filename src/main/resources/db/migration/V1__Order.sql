create table trade
(

    id                bigserial
        constraint trade_pk
            primary key,
    symbol            varchar(10),
    quantity          numeric(20, 8),
    price             numeric(20, 8),
    value             numeric(20, 8),
    side              integer,
    time_in_force     integer,
    order_id          varchar(20),
    strategy          varchar(10),
    order_trigger     integer,
    trigger_direction integer,
    order_status      varchar(10)
);

comment on table trade is 'Orders';

