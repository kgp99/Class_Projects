#include <iostream>
#include <istream>
#include <vector>
#include <string>
#include <cctype>

#include "parser.h"

using namespace std;



void ParserAnalyzer::parse_program() {
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == ID) {
    lexer.UngetToken(t1);
    parse_global_vars();
    parse_body();
  } else if(t1.token_type == LBRACE) {
    lexer.UngetToken(t1);
    parse_body();
  } else {
    grammar_error();
  }

  t1 = lexer.GetToken();
  if (t1.token_type != END_OF_FILE) {
    grammar_error();
  }
}

void ParserAnalyzer::parse_global_vars() {
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == ID) {
    lexer.UngetToken(t1);
    parse_var_decl_list();
  } else {
    grammar_error();
  }
}

void ParserAnalyzer::parse_var_decl_list() {
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == ID) {
    lexer.UngetToken(t1);
    parse_var_decl();
  } else {
    grammar_error();
  }

  t1 = lexer.GetToken();
  if (t1.token_type == ID) {
    lexer.UngetToken(t1);
    parse_var_decl_list();
  } else if (t1.token_type == LBRACE) {
    lexer.UngetToken(t1);
  } else {
    grammar_error();
  }
}

void ParserAnalyzer::parse_var_decl() {
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == ID) {
    lexer.UngetToken(t1);
    parse_var_list();
    t1 = lexer.GetToken();
    if (t1.token_type == COLON) {
      t1 = lexer.GetToken();
      if (t1.token_type == INT || t1.token_type == REAL || t1.token_type == BOOL) {
        add_var_decltovi(t1.token_type);
        t1 = lexer.GetToken();
        if (t1.token_type == SEMICOLON) {
          //End of parsing var_decl Could check next token but would be duplicated.
          //This is where we would put all vars in var list with the type into allvarsinfo
        } else {
          grammar_error();
        }
      } else {
        grammar_error();
      }
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }
}

void ParserAnalyzer::parse_var_list() {
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == ID) {
    //Add ID to temp list
    tempvec.push_back(t1.lexeme);
    Token t2;
    t2 = lexer.GetToken();
    if (t2.token_type == COMMA) {
      parse_var_list();
    } else if (t2.token_type == COLON) {
      //We have to unget due to how the parser is written.
      lexer.UngetToken(t2);
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }
}

void ParserAnalyzer::parse_body() {
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == LBRACE) {
    //I could check for the first of all 4 paths but stmt_list will do that anyways and it's the only path to go, otherwise we'd check.
    parse_stmt_list();
    t1 = lexer.GetToken();
    if (t1.token_type == RBRACE) {
      //Nothing really to do here.
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }
}

//Aka stmt as well
void ParserAnalyzer::parse_stmt_list() {
  Token t1;
  t1 = lexer.GetToken();
  if(t1.token_type == ID) {
    lexer.UngetToken(t1);
    parse_assignment_stmt();
  } else if (t1.token_type == IF) {
    lexer.UngetToken(t1);
    parse_if_stmt();
  } else if (t1.token_type == WHILE) {
    lexer.UngetToken(t1);
    parse_while_stmt();
  } else if (t1.token_type == SWITCH) {
    lexer.UngetToken(t1);
    parse_switch_stmt();
  } else {
      grammar_error();
  }

  t1 = lexer.GetToken();
  if (t1.token_type == ID || t1.token_type == IF || t1.token_type == WHILE || t1.token_type == SWITCH) {
    lexer.UngetToken(t1);
    parse_stmt_list();
  } else if (t1.token_type == RBRACE) {
    lexer.UngetToken(t1);
  } else {
    grammar_error();
  }

}

void ParserAnalyzer::parse_assignment_stmt() {
  int temp3;
  int temp4;
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == ID) {
    temp3 = returntypeofvaroraddvartoavi(t1.lexeme);
    if (temp3 == -1) {
      lhstc = t1.lexeme;
    }
    //cout << "LHE of assignment: " << t1.lexeme << " \n";
    t1 = lexer.GetToken();
    if(t1.token_type == EQUAL) {
      t1 = lexer.GetToken();
      if (t1.token_type == ID || t1.token_type == NUM || t1.token_type == REALNUM || t1.token_type == TRUE || t1.token_type == FALSE || t1.token_type == PLUS || t1.token_type == MINUS || t1.token_type == MULT || t1.token_type == DIV || t1.token_type == LESS || t1.token_type == GREATER || t1.token_type == GTEQ || t1.token_type == LTEQ || t1.token_type == EQUAL || t1.token_type == NOTEQUAL || t1.token_type == NOT) {
        lexer.UngetToken(t1);
        temp4 = parse_expression();
        //The same for certain places.
        //If both are -1 the one at the end would be t4.
        if (temp3 == -1 && temp4 == -1) {
          //Add both to equivalent list if one or two are found then add to equivalent list or merge lists. Print should not really care if the order gets messed up as it would go thru the varlist again and see which match to add to return.
          std::string temp1ID = tempvec.back();
          tempvec.pop_back();
          std::string temp2ID = lhstc;
          //cout << "var 1: " << temp1ID << " var 2: " << temp2ID << " \n";
          bool elft1ID = checkifhasEL(temp1ID);
          bool elft2ID = checkifhasEL(temp2ID);
          if (elft1ID == true && elft2ID == true) {
            mergeELs(temp1ID, temp2ID);
          } else if (elft1ID == true && elft2ID == false) {
            addtoexistingEL(temp1ID, temp2ID);
          } else if (elft2ID == true && elft1ID == false) {
            addtoexistingEL(temp2ID, temp1ID);
          } else {
            //both are false create a new one then.
            EquivalentList tempELtoadd;
            tempELtoadd.eqvL.push_back(temp1ID);
            tempELtoadd.eqvL.push_back(temp2ID);
            Alleqv.push_back(tempELtoadd);
          }
          tempvec.push_back(temp1ID); //I don't think it matters which one I put back but I do need one back for certain cases and for assignments.
        } else if (temp3 == -1 && temp4 != -1) {
          //tempvec.pop_back();
          changettypetorightone(temp4, true);
        } else if (temp4 == -1 && temp3 != -1) {
          //tempvec.pop_back();
          changettypetorightone(temp3, false);
        } else if (temp3 != temp4) {
          cout << "TYPE MISMATCH " << t1.line_no << " C1\n";
          exit(1);
        }
        tempvec.clear();
        //rType right type of an assigment tempI.
        //check for C1. if ltype == rtype then fine if not then TYPE MISMATCH token.line_no C1
        // if any one of lType or rType is -1 then should not throw type mismatch.
        // if lType != -1 && rType is -1 then you search for left ID token to extract its type. searchList should return type.
        // you have to use search list again with the right token to update the right token's type to lType
        t1 = lexer.GetToken();
        //token.Print();
        if (t1.token_type == SEMICOLON) {
          //Nothing to do, end of statment.
        } else {
          grammar_error();
        }
      } else {
        grammar_error();
      }
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }
}

void ParserAnalyzer::parse_if_stmt() {
  int shouldbebool;
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == IF) {
    t1 = lexer.GetToken();
    if (t1.token_type == LPAREN) {
      shouldbebool = parse_expression();
      if (shouldbebool == -1) {
        //has to be ID so change that one ID to type of bool.
        changettypetorightone(3, false);
      } else if (shouldbebool != 3) {
        cout << "TYPE MISMATCH " << t1.line_no << " C4\n";
        exit(1);
      }
      t1 = lexer.GetToken();
      if (t1.token_type == RPAREN) {
        parse_body();
      } else {
        grammar_error();
      }
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }
}

void ParserAnalyzer::parse_while_stmt() {
  int shouldbebool;
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == WHILE) {
    t1 = lexer.GetToken();
    if (t1.token_type == LPAREN) {
      shouldbebool = parse_expression();
      if (shouldbebool == -1) {
        //has to be ID so change that one ID to type of bool.
        changettypetorightone(3, false);
      } else if (shouldbebool != 3) {
        cout << "TYPE MISMATCH " << t1.line_no << " C4\n";
        exit(1);
      }
      tempvec.clear();
      t1 = lexer.GetToken();
      if (t1.token_type == RPAREN) {
        parse_body();
      } else {
        grammar_error();
      }
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }
}

void ParserAnalyzer::parse_switch_stmt() {
  int shouldbeint;
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == SWITCH) {
    t1 = lexer.GetToken();
    if (t1.token_type == LPAREN) {
      shouldbeint = parse_expression();
      if (shouldbeint == -1) {
        //has to be ID so change that one ID to type of int.
        changettypetorightone(1, false);
      } else if (shouldbeint != 1) {
        cout << "TYPE MISMATCH " << t1.line_no << " C5\n";
        exit(1);
      }
      tempvec.clear();
      t1 = lexer.GetToken();
      if (t1.token_type == RPAREN) {
        t1 = lexer.GetToken();
        if (t1.token_type == LBRACE) {
          parse_case_list();
          t1 = lexer.GetToken();
          if (t1.token_type == RBRACE) {
            //nothing else to do here end of switch_stmt.
          } else {
            grammar_error();
          }
        } else {
          grammar_error();
        }
      } else {
        grammar_error();
      }
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }
}
//aka case case*
void ParserAnalyzer::parse_case_list() {
  Token t1;
  t1 = lexer.GetToken();
  if (t1.token_type == CASE) {
    t1 = lexer.GetToken();
    if (t1.token_type == NUM) {
      t1 = lexer.GetToken();
      if (t1.token_type == COLON) {
        parse_body();
      } else {
        grammar_error();
      }
    } else {
      grammar_error();
    }
  } else {
    grammar_error();
  }

  t1 = lexer.GetToken();
  if (t1.token_type == CASE) {
    lexer.UngetToken(t1);
    parse_case_list();
  } else if (t1.token_type == RBRACE) {
    lexer.UngetToken(t1);
  } else {
    grammar_error();
  }
}
//1 = int, 2 = real, 3 = bool, -1 = unknown
int ParserAnalyzer::parse_expression() {
  int temp1;
  int temp2;
  Token t1;
  t1 = lexer.GetToken();
  //t1.Print();
  if (t1.token_type == ID) {
    //Search to get type.
    int rtofvar;
    rtofvar = returntypeofvaroraddvartoavi(t1.lexeme);
    if (rtofvar == -1) {
      tempvec.push_back(t1.lexeme);
    }
    return rtofvar;
  } else if (t1.token_type == NUM) {
    return 1;
  } else if (t1.token_type == REALNUM) {
    return 2;
  } else if (t1.token_type == TRUE || t1.token_type == FALSE) {
    return 3;
  } else if (t1.token_type == PLUS || t1.token_type == MINUS || t1.token_type == MULT || t1.token_type == DIV || t1.token_type == GREATER || t1.token_type == LESS || t1.token_type == GTEQ || t1.token_type == LTEQ || t1.token_type == EQUAL || t1.token_type == NOTEQUAL) {
    //t1.Print();
    temp1 = parse_expression();
    temp2 = parse_expression();

    if (temp1 == -1 && temp2 == -1) {
      //Add both to equivalent list if one or two are found then add to equivalent list or merge lists. Print should not really care if the order gets messed up as it would go thru the varlist again and see which match to add to return.
      std::string temp1ID = tempvec.back();
      tempvec.pop_back();
      std::string temp2ID = tempvec.back();
      tempvec.pop_back();
      //cout << "var 1: " << temp1ID << " var 2: " << temp2ID << " \n";
      bool elft1ID = checkifhasEL(temp1ID);
      bool elft2ID = checkifhasEL(temp2ID);
      if (elft1ID == true && elft2ID == true) {
        mergeELs(temp1ID, temp2ID);
      } else if (elft1ID == true && elft2ID == false) {
        addtoexistingEL(temp1ID, temp2ID);
      } else if (elft2ID == true && elft1ID == false) {
        addtoexistingEL(temp2ID, temp1ID);
      } else {
        //both are false create a new one then.
        EquivalentList tempELtoadd;
        tempELtoadd.eqvL.push_back(temp1ID);
        tempELtoadd.eqvL.push_back(temp2ID);
        Alleqv.push_back(tempELtoadd);
      }
      tempvec.push_back(temp1ID); //I don't think it matters which one I put back but I do need one back for certain cases and for assignments.
    } else if (temp1 == -1 && temp2 != -1) {
      changettypetorightone(temp2, false);
    } else if (temp2 == -1 && temp1 != -1) {
      changettypetorightone(temp1, false);
    } else if (temp1 != temp2) {
      cout << "TYPE MISMATCH " << t1.line_no << " C2\n";
      exit(1);
    }

    if (t1.token_type == PLUS || t1.token_type == MINUS || t1.token_type == MULT || t1.token_type == DIV) {
      //return the type of one or the other as both should be the same type or it would have have a type error and the program would have exited.
      return temp1;
    } else {
      //can only be the rest that are not in the if stament for match else that only return bools.
      return 3;
    }

    return 1;
  } else if (t1.token_type == NOT) {
    //t1.Print();
    temp1 = parse_expression();
    if (temp1 == -1) {
      //has to be ID so change that one ID to type of bool.
      changettypetorightone(3, false);
    } else if (temp1 != 3) {
      cout << "TYPE MISMATCH " << t1.line_no << " C3\n";
      exit(1);
    }
    return 3;
  } else {
    cout << "Syntax error in expression";
    grammar_error();
    return -2; // really just here to make sure warning doesn't show up, not sure they this isn't a warning as well as it won't ever come here.
  }
}

//Should not be used but just here from P2 and to check if parser is correct.
void ParserAnalyzer::grammar_error() {
  cout << "Syntax Error\n";
  exit(1);
}

void ParserAnalyzer::add_var_decltovi(TokenType tempTT) {
  Var_Type VTtemp;
  if (tempTT == INT) {
    VTtemp.typeofv = inttname;
  } else if (tempTT == REAL) {
    VTtemp.typeofv = realtname;
  } else if (tempTT == BOOL) {
    VTtemp.typeofv = booltname;
  } else {
    cout << "Something went wrong. \n";
  }
  VTtemp.inaequivalencelist = false;
  VTtemp.printedornot = false;
  for (auto tempvn : tempvec) {
    VTtemp.varname = tempvn;
    allvarsinfo.push_back(VTtemp);
  }
  tempvec.clear();
}

void ParserAnalyzer::printrtypesoreqtypes() {
  for (auto tempVT : allvarsinfo) {
    if (tempVT.typeofv == inttname || tempVT.typeofv == realtname || tempVT.typeofv == booltname) {
      cout << tempVT.varname << ": " << tempVT.typeofv << " #\n";
      //Could also change printed to true but we'll see if it's needed.
    } else {
      //cout << tempVT.printedornot;
      if (tempVT.printedornot == false) {
        printEL(tempVT.varname);
      }
    }
  }
}

void ParserAnalyzer::printEL(std::string vartoprintEL) {
  int isfirstE = 1;
  bool hasEL;
  hasEL = checkifhasEL(vartoprintEL);
  if (hasEL) {
    std::string printEL = "";
    std::vector<std::string> ELtoprint;
    ELtoprint = getthatEL(vartoprintEL);
    for (auto tempVTpELIT = allvarsinfo.begin(); tempVTpELIT != allvarsinfo.end(); ++tempVTpELIT) {
      Var_Type tempVT = *tempVTpELIT;
      for (auto ELvec : ELtoprint) {
        if (tempVT.varname == ELvec) {
          if(isfirstE == 1) {
            printEL += tempVT.varname;
            isfirstE++;
          } else {
            printEL += ", " + tempVT.varname;
          }
          tempVTpELIT->printedornot = true;
          //cout << tempVTpEL.printedornot;
        }
      }
    }
    cout << printEL << ": ? #\n";
  } else {
    cout << vartoprintEL << ": ? #\n";
  }
}

bool ParserAnalyzer::checkifhasEL(std::string vartofindinELV) {
  for (auto tempEL : Alleqv) {
    std::vector<std::string> tempELvecs = tempEL.eqvL;
    for (auto tempeqvL : tempELvecs) {
      if (tempeqvL == vartofindinELV) {
        return true;
      }
    }
  }
  return false;
}

std::vector<std::string> ParserAnalyzer::getthatEL(std::string getELofthisS) {
  for (auto tempEL : Alleqv) {
    std::vector<std::string> tempELvecs = tempEL.eqvL;
    for (auto tempeqvL : tempELvecs) {
      if (tempeqvL == getELofthisS) {
        return tempELvecs;
      }
    }
  }
  //Should never come here so ignore compiler warnning.
}

std::vector<std::string> ParserAnalyzer::getthatELanderaseit(std::string getvectomerge) {
  for (auto tempELit = Alleqv.begin(); tempELit != Alleqv.end(); ++tempELit) {
    EquivalentList tempELtoD = *tempELit;
    std::vector<std::string> tempELvecs = tempELtoD.eqvL;
    for (auto tempeqvL : tempELvecs) {
      if (tempeqvL == getvectomerge) {
        Alleqv.erase(tempELit);
        return tempELvecs;
      }
    }
  }
}

void ParserAnalyzer::mergeELs(std::string hasEL, std::string alsohasEL) {
  EquivalentList tempmergeEL;
  std::vector<std::string> el1 = getthatELanderaseit(hasEL);
  //Check to see if they are alreayd in the same EL then just put back the erased list then return.
  for (auto cstringtemp1 : el1) {
    if (cstringtemp1 == alsohasEL) {
      for (auto stringtemp1 : el1) {
        tempmergeEL.eqvL.push_back(stringtemp1);
      }
      Alleqv.push_back(tempmergeEL);
      return;
    }
  }
  std::vector<std::string> el2 = getthatELanderaseit(alsohasEL);
  for (auto stringtemp1 : el1) {
    tempmergeEL.eqvL.push_back(stringtemp1);
  }
  for (auto stringtemp2 : el2) {
    tempmergeEL.eqvL.push_back(stringtemp2);
  }
  Alleqv.push_back(tempmergeEL);
}

void ParserAnalyzer::addtoexistingEL(std::string IDthathasEL, std::string IDtoaddtoEL) {

  for (auto tempELita = Alleqv.begin(); tempELita != Alleqv.end(); ++tempELita) {
    std::vector<std::string> tempELvecs = tempELita->eqvL;
    for (auto tempeqvL : tempELvecs) {
      if (tempeqvL == IDthathasEL) {
        tempELita->eqvL.push_back(IDtoaddtoEL);
      }
    }
  }
}

void ParserAnalyzer::changettypetorightone(int typetochangetoint, bool uselhsortempvec) {
  std::string idtochange;
  if (uselhsortempvec) {
    idtochange = lhstc;
  } else {
    idtochange = tempvec.back();
    //tempvec.pop_back();
  }
  std::string typetochangetostring;
  //cout << "idtochange: " << idtochange;
  //cout << "change to type: " << typetochangetoint << ", idtochange: " << idtochange << "\n";
  if (typetochangetoint == 1) {
    typetochangetostring = inttname;
  } else if (typetochangetoint == 2) {
    typetochangetostring = realtname;
  } else {
    typetochangetostring = booltname;
  }
  bool hasELtoct = checkifhasEL(idtochange);
  if (hasELtoct) {
    std::vector<std::string> changealleqv = getthatELanderaseit(idtochange);
    for (auto tempVTittc1 = allvarsinfo.begin(); tempVTittc1 != allvarsinfo.end(); ++tempVTittc1) {
      for (auto eqvID : changealleqv) {
        if (tempVTittc1->varname == eqvID) {
          tempVTittc1->typeofv = typetochangetostring;
        }
      }
    }
  } else {
    for (auto tempVTittc2 = allvarsinfo.begin(); tempVTittc2 != allvarsinfo.end(); ++tempVTittc2) {
      Var_Type tempVTtocheck = *tempVTittc2;
      if (tempVTtocheck.varname == idtochange) {
        //cout << "change one var to certain type: " << typetochangetostring << "\n";
        tempVTittc2->typeofv = typetochangetostring;
      }
    }
  }
}

int ParserAnalyzer::returntypeofvaroraddvartoavi(std::string fvt) {
  for (auto tempVT : allvarsinfo) {
    if (tempVT.varname == fvt) {
      if (tempVT.typeofv == inttname) {
        return 1;
      } else if (tempVT.typeofv == realtname) {
        return 2;
      } else if (tempVT.typeofv == booltname) {
        return 3;
      } else if (tempVT.typeofv == unknowntname) {
        return -1;
      }
    }
  }
  //Can't find var so add var to list of vars as unknown.
  Var_Type VTtemp1;
  VTtemp1.varname = fvt;
  VTtemp1.typeofv = unknowntname;
  VTtemp1.inaequivalencelist = false;
  VTtemp1.printedornot = false;
  allvarsinfo.push_back(VTtemp1);
  return -1;
}

int main()
{
    ParserAnalyzer parserA;
    parserA.parse_program();
    parserA.printrtypesoreqtypes();

}
