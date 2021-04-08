Feature: Test calculator

  Scenario: Sum two numbers
    Given I have numbers 2 and 3
    When I want to sum
    Then I should have result 5