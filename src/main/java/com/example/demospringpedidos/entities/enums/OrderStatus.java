package com.example.demospringpedidos.entities.enums;

public enum OrderStatus {

    /*
    A forma mais simples de declarar enums é simplesmente listando os valores válidos, conforme mostrado abaixo

    WAITING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELED

    Isso dificulta a manutenção: o código numérico seria atribuído automaticamente e salvo no banco, em vez do nome.
    Se um valor for inserido no meio do enum, os códigos posteriores mudam e os registros existentes ficam incorretos.
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
