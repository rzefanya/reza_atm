# Reza ATM

Reza ATM is ATM simulation CLI application

## Usage

Simply Run AtmApplication.java and start using commands

```console
shell:>login <name>
login or create account

shell:>deposit <amount>
deposit an amount of money to the logged in account
will automatically pay debt if exists

shell:>withdraw <amount>
withdraw an amount of money from the logged in account
cannot exceed current balance

shell:>transfer <target> <amount>
transfer an amount of money to target
exceeding current balance will create debt from logged in account to target
transferring money to account who has debt to the logged in account will reduce their debt

shell:>logout
logout current logged in account

```