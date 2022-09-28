#ifndef __PARSER__H__
#define __PARSER__H__

#include <iostream>
#include <istream>
#include <vector>
#include <string>
#include <cctype>

#include "lexer.h"


class Var_Type {
  public:
    std::string varname;
    std::string typeofv;
    bool inaequivalencelist; //Seemed good at first but updating this seemed like a pain
    bool printedornot; //Really should be used only if the var is unknown and in an equivalent list.
};

class EquivalentList {
  public:
    std::vector<std::string> eqvL;
};

class ParserAnalyzer {
  public:
    std::vector<Var_Type> allvarsinfo; //Here to hold type info and name of var and if they are in a equivalent, also makes sure the order we print them out in is right, should only be in a equivalent list if the type is unknown.
    //Once type gets updated for a var in a equivalent list then update all other vars in found equivalent list then erase the equivalent list.
    std::vector<EquivalentList> Alleqv; //Should be used for those vars that are unknown but we know what they have to be the same as.
    void parse_program();
    void printrtypesoreqtypes();
  private:
    std::string inttname = "int";
    std::string realtname = "real";
    std::string booltname = "bool";
    std::string unknowntname = "?";
    LexicalAnalyzer lexer;
    std::vector<std::string> tempvec;
    std::string lhstc;
    int indexofLHSVT;
    void parse_global_vars();
    void parse_var_decl_list();
    void parse_var_decl();
    void parse_var_list();
    //No need for one that is for type_name
    void parse_body();
    void parse_stmt_list();
    //void parse_stmt(); There is no unque statment and stmt_list is a repeated statment so no point
    void parse_assignment_stmt();
    void parse_if_stmt();
    void parse_while_stmt();
    void parse_switch_stmt();
    void parse_case_list();
    //void parse_case();
    int parse_expression(); //Might need something for the case when this comes from expression, as we need the lhs's Var_Type info. it could be a temp global var int that holds lhs index.
    //no need for a unary_operator or binary_operator or primary.
    void grammar_error();
    void add_var_decltovi(TokenType); //We could have just done this in var_decl_list as well.
    int returntypeofvaroraddvartoavi(std::string);
    int getIndexofVarinallvarsinfo(std::string);
    bool checkifhasEL(std::string);
    std::vector<std::string> getthatEL(std::string);
    void mergeELs(std::string, std::string);
    std::vector<std::string> getthatELanderaseit(std::string);
    void addtoexistingEL(std::string, std::string);
    void changettypetorightone(int, bool);

    void printEL(std::string);
};



#endif  // __PARSER__H__
