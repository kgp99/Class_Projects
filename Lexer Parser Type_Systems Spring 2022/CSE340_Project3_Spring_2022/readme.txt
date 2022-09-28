Seems like scope does not matter, the var in the whole program must be the same type. No block scopes or explicit declarations.

Need to save which are equivalent, in what order do they appear, line number maybe but fast fail so we don't need to do this.

Var list: name, type

equivalent list: vector of strings

equivalent in all: vector of equivalent list

Fast fail for errors if we find it, during the program if something from unknown changes to a type then update all vars in same equivalent list. So a double for loop search forwards, return that vector it was found in so inner vector
then call another method to change or find those in equivalent list if they are unknown only, if they are known then it just checks for the type of that var. Merge two unknown lists together if both sides are unknown, remove one of the lists.

lexer should now work with all needed tokens and not read the wrong token.

Parser should work now. Read comments in code or the assignment or given lexer for the next parts.

Return type of the first or second operand should be looked at first to decide next step or if we need a return type from that operand:

if( return same type ) then we need the type or var names
if ( return bool ) then we just need to know the type is bool
Defiantly more cases but just an example.

Should be expression returns for if while and switch
Should be expression mismatch within the expression for various reasons.
Should be expression mismatch for lhs and rhs.

Extra credit: find out what 15 cases are failing on gradescope.


Test 78 (0.0/1.25) passes now after change on the pop_back comment out in method changettypetorightone
don't know why exactly though.

Test passes probably failing as I need to get the first added var for if, while and switch statements to change otherwise the wrong value is being updated.
Other then that I can't see where else the program would fail expect in parse_expression. 
