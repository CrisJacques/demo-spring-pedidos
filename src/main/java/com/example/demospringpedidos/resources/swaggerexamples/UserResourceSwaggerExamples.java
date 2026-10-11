package com.example.demospringpedidos.resources.swaggerexamples;

public final class UserResourceSwaggerExamples {

    private UserResourceSwaggerExamples() {}

    public static final String GET_USERS_RESPONSE_SUCCESS = """
                            [
                              {
                                "id": 1,
                                "name": "Maria Brown",
                                "email": "maria@gmail.com",
                                "phone": "988888888"
                              }
                            ]
                            """;

    public static final String GET_USER_BY_ID_RESPONSE_SUCCESS = """
                            {
                              "id": 1,
                              "name": "Maria Brown",
                              "email": "maria@gmail.com",
                              "phone": "988888888"
                            }
                            """;

    public static final String POST_USERS_REQUEST = """
                    {
                      "name": "João da Silva",
                      "email": "joao@example.com",
                      "phone": "999999999",
                      "password": "Abcdefg1"
                    }
                    """;

    public static final String PUT_USER_REQUEST = """
                                                       {
                                                         "name": "Maria Brown",
                                                         "email": "maria.brown@example.com",
                                                         "phone": "988888888",
                                                         "password": "Abcdefg1"
                                                       }
                                                       """;
}
