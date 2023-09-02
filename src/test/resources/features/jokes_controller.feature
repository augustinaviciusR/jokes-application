Feature: Joke management

  Scenario: Search jokes with valid query
    Given I have a valid query "funny"
    When I search for jokes with this query
    Then I should get a list of jokes

  Scenario: Search jokes with invalid query
    Given I have an invalid query "fu"
    When I search for jokes with this query
    Then I should get an error message

  Scenario: Search jokes with query exceeding 100 characters
    Given I have a query that exceeds 100 characters
    When I search for jokes with this query
    Then I should get an error message

  Scenario: Get a joke without category
    When I request a joke without category
    Then I should get a random joke

  Scenario: Get a joke with valid category
    Given I have a valid category "animal"
    When I request a joke with this category
    Then I should get a joke from the "animal" category

  Scenario: Get a joke with invalid category
    Given I have an invalid category "unknown"
    When I request a joke with this category
    Then I should get an error message
