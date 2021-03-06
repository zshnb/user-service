create table `user`
(
    `id`          int(11) AUTO_INCREMENT PRIMARY KEY,
    `name`        varchar(255) not null,
    `description` varchar(255) not null default '',
    `address`     varchar(255) not null default '',
    `create_at`   datetime DEFAULT CURRENT_TIMESTAMP,
    `dob`         datetime
);

create table `follow`
(
    `id`             int(11) AUTO_INCREMENT PRIMARY KEY,
    `user_id`        int(11),
    `follow_user_id` int(11)
);

create table `fan`
(
    `id`          int(11) AUTO_INCREMENT PRIMARY KEY,
    `user_id`     int(11),
    `fan_user_id` int(11)
);
