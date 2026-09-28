CREATE TABLE ciudades (
    ciud_id     INT PRIMARY KEY AUTO_INCREMENT,
    ciud_nombre VARCHAR(100) NOT NULL
);

CREATE TABLE personas (
    dir_tel      VARCHAR(15) PRIMARY KEY,
    dir_tipo_tel VARCHAR(20),
    dir_nombre   VARCHAR(100) NOT NULL,
    dir_direccion VARCHAR(150),
    dir_ciud_id  INT,
    FOREIGN KEY (dir_ciud_id) REFERENCES ciudades(ciud_id)
);