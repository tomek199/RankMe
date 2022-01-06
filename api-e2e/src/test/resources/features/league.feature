Feature: League

  Background: I init and cleanup database
    Given I cleanup database

  Scenario: Show empty leagues list
    Then I have no leagues

  Scenario: Create new league and change league data
    * I create league "Star Wars"
    * I use league "Star Wars"
    When I change league settings to allow draws true and max score 5
    * I rename league to "Transformers"
    Then I have league "Transformers" with allow draws true and max score 5

  Scenario: Create league with players
    * I create league "Transformers"
    * I use league "Transformers"
    When I create player "Optimus Prime"
    * I create player "Bumblebee"
    Then I have players in league:
      | name          | deviation | rating |
      | Optimus Prime | 350       | 1500   |
      | Bumblebee     | 350       | 1500   |

  Scenario: Create league with games
    * I create league "Star Wars"
    * I use league "Star Wars"
    * I create player "Han Solo"
    * I create player "Darth Vader"
    When I play 6 games between "Han Solo" and "Darth Vader"
    Then I have first 4 of 6 games connected in league

  Scenario: Create multiple leagues to check pagination
    When I create 10 leagues
    Then I have first 2 of 10 leagues listed
    * I have first 6 of 10 leagues listed
    * I have first 10 of 10 leagues listed
    * I have first 3 after 4 of 10 leagues listed
    * I have first 5 after 3 of 10 leagues listed
    * I have first 4 after 6 of 10 leagues listed
