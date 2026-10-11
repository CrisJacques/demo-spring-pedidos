package com.example.demospringpedidos.resources.swaggerexamples;

public final class ProductResourceSwaggerExamples {

    public static final String GET_PRODUCTS_RESPONSE_SUCCESS = """
                            [
                              {
                                "id": 1,
                                "name": "The Lord of the Rings",
                                "description": "Lorem ipsum dolor sit amet, consectetur.",
                                "price": 90.5,
                                "imgUrl": "https://example.com/images/lord-of-the-rings.jpg"
                              }
                            ]
                            """;

    public static final String GET_PRODUCT_BY_ID_RESPONSE_SUCCESS = """
                            {
                              "id": 1,
                              "name": "The Lord of the Rings",
                              "description": "Lorem ipsum dolor sit amet, consectetur.",
                              "price": 90.5,
                              "imgUrl": "https://example.com/images/lord-of-the-rings.jpg"
                            }
                            """;

    public static final String POST_PRODUCTS_REQUEST = """
                            {
                              "name": "Notebook Gamer",
                              "description": "Notebook para jogos e trabalho.",
                              "price": 5550.0,
                              "imgUrl": "",
                              "categories": [
                                {
                                  "id": 3
                                }
                              ]
                            }
                    """;

    public static final String PUT_PRODUCT_REQUEST = """
                                                               {
                                                                 "name": "Notebook Gamer",
                                                                 "description": "Notebook para jogos e trabalho.",
                                                                 "price": 6550.99,
                                                                 "imgUrl": "",
                                                                 "categories": [
                                                                   {
                                                                     "id": 3
                                                                   }
                                                                 ]
                                                               }
                                                       """;
}
