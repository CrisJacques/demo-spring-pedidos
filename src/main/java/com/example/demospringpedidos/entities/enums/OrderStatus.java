package com.example.demospringpedidos.entities.enums;

public enum OrderStatus {

    /*
    A forma mais simples de declarar enums é simplesmente listando os valores válidos, conforme mostrado abaixo

    WAITING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELED

    Porém, isso vai ser um problema para dar manutenção no futuro, pois dessa forma a atribuição de um código numérico será feita automaticamente
    e no banco de dados é salvo o valor numérico da opção, e não o seu nome. Dessa forma, se futuramente alguém colocar um valor novo no meio do enum,
    vai mudar o código numérico das opções posteriores ao valor adicionado, e os registros já existentes no banco de dados ficarão errados.
    Então a melhor opção é atribuir manualmente os códigos numéricos das opções, para evitar esse risco.

    Segue abaixo a forma mais correta de declarar um enum
     */

    WAITING_PAYMENT(1),
    PAID(2),
    SHIPPED(3),
    DELIVERED(4),
    CANCELED(5);

    private int code;

    // O construtor de tipos enumerados é private!!! É um caso especial
    private OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static OrderStatus valueOf(int code) {
        for (OrderStatus value : OrderStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus code");
    }

}
