package com.example.demospringpedidos.resources.swaggerexamples;

public final class OrderResourceSwaggerExamples {

    private OrderResourceSwaggerExamples() {}

    public static final String GET_ORDERS_RESPONSE_SUCCESS = """
                            [
                              {
                                "id": 1,
                                "moment": "2019-06-20T19:53:07Z",
                                "orderStatus": "PAID",
                                "client": {
                                  "id": 1,
                                  "name": "Maria Brown",
                                  "email": "maria@gmail.com",
                                  "phone": "988888888"
                                },
                                "items": [
                                  {
                                    "product": {
                                      "id": 1,
                                      "name": "The Lord of the Rings",
                                      "description": "Lorem ipsum dolor sit amet, consectetur.",
                                      "price": 90.5,
                                      "imgUrl": "",
                                      "categories": [{"id": 2, "name": "Books"}]
                                    },
                                    "quantity": 2,
                                    "price": 90.5,
                                    "subTotal": 181.0
                                  },
                                  {
                                    "product": {
                                      "id": 3,
                                      "name": "Macbook Pro",
                                      "description": "Nam eleifend maximus tortor, at mollis.",
                                      "price": 1250.0,
                                      "imgUrl": "",
                                      "categories": [{"id": 3, "name": "Computers"}]
                                    },
                                    "quantity": 1,
                                    "price": 1250.0,
                                    "subTotal": 1250.0
                                  }
                                ],
                                "payment": {
                                  "id": 1,
                                  "moment": "2019-06-20T19:53:07Z"
                                },
                                "total": 1431.0
                              }
                            ]
                            """;

    public static final String GET_ORDER_BY_ID_RESPONSE_SUCCESS = """
                            {
                              "id": 1,
                              "moment": "2019-06-20T19:53:07Z",
                              "orderStatus": "PAID",
                              "client": {
                                "id": 1,
                                "name": "Maria Brown",
                                "email": "maria@gmail.com",
                                "phone": "988888888"
                              },
                              "items": [
                                {
                                  "product": {
                                    "id": 1,
                                    "name": "The Lord of the Rings",
                                    "description": "Lorem ipsum dolor sit amet, consectetur.",
                                    "price": 90.5,
                                    "imgUrl": "",
                                    "categories": [{"id": 2, "name": "Books"}]
                                  },
                                  "quantity": 2,
                                  "price": 90.5,
                                  "subTotal": 181.0
                                },
                                {
                                  "product": {
                                    "id": 3,
                                    "name": "Macbook Pro",
                                    "description": "Nam eleifend maximus tortor, at mollis.",
                                    "price": 1250.0,
                                    "imgUrl": "",
                                    "categories": [{"id": 3, "name": "Computers"}]
                                  },
                                  "quantity": 1,
                                  "price": 1250.0,
                                  "subTotal": 1250.0
                                }
                              ],
                              "payment": {
                                "id": 1,
                                "moment": "2019-06-20T19:53:07Z"
                              },
                              "total": 1431.0
                            }
                            """;

    public static final String POST_ORDER_REQUEST = """
                            {
                              "clientId": 1,
                              "items": [
                                {"productId": 1, "quantity": 2},
                                {"productId": 3, "quantity": 1}
                              ]
                            }
                            """;

    public static final String PUT_ORDER_ERROR_422 = """
                                    {
                                      "timestamp": "2026-10-10T23:51:14Z",
                                      "status": 422,
                                      "error": "Business error",
                                      "message": "Invalid OrderStatus code",
                                      "path": "/orders/2"
                                    }
                                    """;

    public static final String PUT_ORDER_REQUEST = """
                                                                {
                                                                  "orderStatus": 2,
                                                                  "items": [
                                                                    {
                                                                      "productId": 1,
                                                                      "quantity": 3
                                                                    },
                                                                    {
                                                                      "productId": 3,
                                                                      "quantity": 4
                                                                    }
                                                                  ]
                                                                }
                                                       """;

}
