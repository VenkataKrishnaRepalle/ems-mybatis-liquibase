Feature: User Implementation

  Scenario: User Should be login
    Given baseUrl + '/auth/login'
    And request
    """
    {
      "email": "admin@gmail.com",
      "password": "admin"
    }
    """
    When method POST
    Then status 200
    And print response