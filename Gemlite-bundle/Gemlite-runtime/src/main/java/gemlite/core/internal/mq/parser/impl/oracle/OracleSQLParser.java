/*                                                                         
 * Copyright 2010-2013 the original author or authors.                     
 *                                                                         
 * Licensed under the Apache License, Version 2.0 (the "License");         
 * you may not use this file except in compliance with the License.        
 * You may obtain a copy of the License at                                 
 *                                                                         
 *      http://www.apache.org/licenses/LICENSE-2.0                         
 *                                                                         
 * Unless required by applicable law or agreed to in writing, software     
 * distributed under the License is distributed on an "AS IS" BASIS,       
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and     
 * limitations under the License.                                          
 */                                                                        
package gemlite.core.internal.mq.parser.impl.oracle;
import gemlite.core.internal.domain.DomainRegistry;
import gemlite.core.internal.mq.MqConstant;
import gemlite.core.internal.mq.domain.ParseredValue;
import gemlite.core.internal.mq.parser.ISQLParserDelegator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class OracleSQLParser extends Parser implements ISQLParserDelegator {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND_SYM", "A_", "BIT_NUM", "B_", "COMMA", "C_", "DELETE_SYM", "DOT", "D_", "EQ_SYM", "EXPONENT", "E_", "FALSE_SYM", "FLOAT", "FROM", "F_", "G_", "HEX_DIGIT", "HEX_DIGIT_FRAGMENT", "H_", "ID", "INSERT", "INT", "INTO", "IS_SYM", "I_", "J_", "K_", "LPAREN", "L_", "M_", "NULL_SYM", "N_", "O_", "P_", "Q_", "RPAREN", "R_", "SET_SYM", "STRING", "S_", "TIMESTAMP", "TODATE", "TRI_COLON", "TRUE_SYM", "T_", "UNDERSCORE", "UPDATE", "U_", "VALUES", "VALUE_SYM", "V_", "WHERE", "WS", "W_", "X_", "Y_", "Z_"
    };

    public static final int EOF=-1;
    public static final int AND_SYM=4;
    public static final int A_=5;
    public static final int BIT_NUM=6;
    public static final int B_=7;
    public static final int COMMA=8;
    public static final int C_=9;
    public static final int DELETE_SYM=10;
    public static final int DOT=11;
    public static final int D_=12;
    public static final int EQ_SYM=13;
    public static final int EXPONENT=14;
    public static final int E_=15;
    public static final int FALSE_SYM=16;
    public static final int FLOAT=17;
    public static final int FROM=18;
    public static final int F_=19;
    public static final int G_=20;
    public static final int HEX_DIGIT=21;
    public static final int HEX_DIGIT_FRAGMENT=22;
    public static final int H_=23;
    public static final int ID=24;
    public static final int INSERT=25;
    public static final int INT=26;
    public static final int INTO=27;
    public static final int IS_SYM=28;
    public static final int I_=29;
    public static final int J_=30;
    public static final int K_=31;
    public static final int LPAREN=32;
    public static final int L_=33;
    public static final int M_=34;
    public static final int NULL_SYM=35;
    public static final int N_=36;
    public static final int O_=37;
    public static final int P_=38;
    public static final int Q_=39;
    public static final int RPAREN=40;
    public static final int R_=41;
    public static final int SET_SYM=42;
    public static final int STRING=43;
    public static final int S_=44;
    public static final int TIMESTAMP=45;
    public static final int TODATE=46;
    public static final int TRI_COLON=47;
    public static final int TRUE_SYM=48;
    public static final int T_=49;
    public static final int UNDERSCORE=50;
    public static final int UPDATE=51;
    public static final int U_=52;
    public static final int VALUES=53;
    public static final int VALUE_SYM=54;
    public static final int V_=55;
    public static final int WHERE=56;
    public static final int WS=57;
    public static final int W_=58;
    public static final int X_=59;
    public static final int Y_=60;
    public static final int Z_=61;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public OracleSQLParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public OracleSQLParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return OracleSQLParser.tokenNames; }
    public String getGrammarFileName() { return "D:\\TJSIB\\SQL.g"; }


    public boolean isValidTableName(String tableName)
    {
  	  String regionName = DomainRegistry.tableToRegion(tableName);
  	  if(regionName == null || regionName.equals(""))
  		  return false;
  	  else
  		  return true;
    }

    // $ANTLR start "parsed_value_list"
    // D:\\TJSIB\\SQL.g:114:1: parsed_value_list returns [List<ParseredValue> result] : package1= parsed_one_package (package2= parsed_one_package )* ;
    public final List<ParseredValue> parsed_value_list() throws RecognitionException {
        List<ParseredValue> result = null;


        List<ParseredValue> package1 =null;

        List<ParseredValue> package2 =null;


        try {
            // D:\\TJSIB\\SQL.g:115:2: (package1= parsed_one_package (package2= parsed_one_package )* )
            // D:\\TJSIB\\SQL.g:115:4: package1= parsed_one_package (package2= parsed_one_package )*
            {
            pushFollow(FOLLOW_parsed_one_package_in_parsed_value_list1066);
            package1=parsed_one_package();

            state._fsp--;



            		result = new ArrayList<ParseredValue>();
            	 	result.addAll(package1);	
            	  

            // D:\\TJSIB\\SQL.g:120:2: (package2= parsed_one_package )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==DELETE_SYM||LA1_0==INSERT||LA1_0==UPDATE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\TJSIB\\SQL.g:121:4: package2= parsed_one_package
            	    {
            	    pushFollow(FOLLOW_parsed_one_package_in_parsed_value_list1081);
            	    package2=parsed_one_package();

            	    state._fsp--;



            	    		result.addAll(package2);	
            	    	  

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "parsed_value_list"



    // $ANTLR start "parsed_one_package"
    // D:\\TJSIB\\SQL.g:128:1: parsed_one_package returns [List<ParseredValue> result] : s1= sql_statement (s2= sql_statement )* ;
    public final List<ParseredValue> parsed_one_package() throws RecognitionException {
        List<ParseredValue> result = null;


        ParseredValue s1 =null;

        ParseredValue s2 =null;


        try {
            // D:\\TJSIB\\SQL.g:129:2: (s1= sql_statement (s2= sql_statement )* )
            // D:\\TJSIB\\SQL.g:130:2: s1= sql_statement (s2= sql_statement )*
            {
            pushFollow(FOLLOW_sql_statement_in_parsed_one_package1110);
            s1=sql_statement();

            state._fsp--;



            		result = new ArrayList<ParseredValue>();
            		if(s1 != null)
                  		{
            			result.add(s1);
                  		}
            	

            // D:\\TJSIB\\SQL.g:138:2: (s2= sql_statement )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // D:\\TJSIB\\SQL.g:138:3: s2= sql_statement
            	    {
            	    pushFollow(FOLLOW_sql_statement_in_parsed_one_package1119);
            	    s2=sql_statement();

            	    state._fsp--;



            	    		if(s2 != null)
            	          		{
            	    			result.add(s2);
            	          		}
            	    	

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "parsed_one_package"



    // $ANTLR start "sql_statement"
    // D:\\TJSIB\\SQL.g:148:1: sql_statement returns [ParseredValue pv] : ( insert_statement | update_statement | delete_statement );
    public final ParseredValue sql_statement() throws RecognitionException {
        ParseredValue pv = null;


        ParseredValue insert_statement1 =null;

        ParseredValue update_statement2 =null;

        ParseredValue delete_statement3 =null;


        try {
            // D:\\TJSIB\\SQL.g:149:2: ( insert_statement | update_statement | delete_statement )
            int alt3=3;
            switch ( input.LA(1) ) {
            case INSERT:
                {
                alt3=1;
                }
                break;
            case UPDATE:
                {
                alt3=2;
                }
                break;
            case DELETE_SYM:
                {
                alt3=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }

            switch (alt3) {
                case 1 :
                    // D:\\TJSIB\\SQL.g:149:4: insert_statement
                    {
                    pushFollow(FOLLOW_insert_statement_in_sql_statement1139);
                    insert_statement1=insert_statement();

                    state._fsp--;


                    pv =insert_statement1;

                    }
                    break;
                case 2 :
                    // D:\\TJSIB\\SQL.g:150:4: update_statement
                    {
                    pushFollow(FOLLOW_update_statement_in_sql_statement1146);
                    update_statement2=update_statement();

                    state._fsp--;


                    pv =update_statement2;

                    }
                    break;
                case 3 :
                    // D:\\TJSIB\\SQL.g:151:4: delete_statement
                    {
                    pushFollow(FOLLOW_delete_statement_in_sql_statement1153);
                    delete_statement3=delete_statement();

                    state._fsp--;


                    pv =delete_statement3;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return pv;
    }
    // $ANTLR end "sql_statement"



    // $ANTLR start "delete_statement"
    // D:\\TJSIB\\SQL.g:154:1: delete_statement returns [ParseredValue pv] : DELETE_SYM FROM table_spec where_clause ;
    public final ParseredValue delete_statement() throws RecognitionException {
        ParseredValue pv = null;


        String table_spec4 =null;

        HashMap where_clause5 =null;


        try {
            // D:\\TJSIB\\SQL.g:155:2: ( DELETE_SYM FROM table_spec where_clause )
            // D:\\TJSIB\\SQL.g:156:2: DELETE_SYM FROM table_spec where_clause
            {
            match(input,DELETE_SYM,FOLLOW_DELETE_SYM_in_delete_statement1170); 

            match(input,FROM,FOLLOW_FROM_in_delete_statement1172); 

            pushFollow(FOLLOW_table_spec_in_delete_statement1174);
            table_spec4=table_spec();

            state._fsp--;


            pushFollow(FOLLOW_where_clause_in_delete_statement1177);
            where_clause5=where_clause();

            state._fsp--;



            		pv = new ParseredValue();
                  		pv.setOp(MqConstant.DELETE);
                  		String tableName = table_spec4;
            	 	if(!isValidTableName(tableName))
                			return null;
            	    	pv.setTableName(tableName);
            	    	
            	    	pv.getValueMap().putAll(where_clause5);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return pv;
    }
    // $ANTLR end "delete_statement"



    // $ANTLR start "update_statement"
    // D:\\TJSIB\\SQL.g:170:1: update_statement returns [ParseredValue pv] : UPDATE table_spec set_columns_clause where_clause ;
    public final ParseredValue update_statement() throws RecognitionException {
        ParseredValue pv = null;


        String table_spec6 =null;

        HashMap set_columns_clause7 =null;

        HashMap where_clause8 =null;


        try {
            // D:\\TJSIB\\SQL.g:171:2: ( UPDATE table_spec set_columns_clause where_clause )
            // D:\\TJSIB\\SQL.g:172:2: UPDATE table_spec set_columns_clause where_clause
            {
            match(input,UPDATE,FOLLOW_UPDATE_in_update_statement1196); 

            pushFollow(FOLLOW_table_spec_in_update_statement1198);
            table_spec6=table_spec();

            state._fsp--;


            pushFollow(FOLLOW_set_columns_clause_in_update_statement1201);
            set_columns_clause7=set_columns_clause();

            state._fsp--;


            pushFollow(FOLLOW_where_clause_in_update_statement1204);
            where_clause8=where_clause();

            state._fsp--;



            		pv = new ParseredValue();
            		pv.setOp(MqConstant.UPDATE);
            		String tableName = table_spec6;
            		
            		if(!isValidTableName(tableName))
                			return null;
                		pv.setTableName(tableName);
                		pv.getUpdateMap().putAll(set_columns_clause7);
            	        pv.getValueMap().putAll(set_columns_clause7);
            	        
            	        pv.getWhereMap().putAll(where_clause8);
            	        for(Iterator it = where_clause8.keySet().iterator(); it.hasNext();)
            	        {
            	            String k = (String) it.next();
            	            String v = (String)where_clause8.get(k);
            	            if(!pv.getValueMap().containsKey(k))
            	                pv.getValueMap().put(k, v);
            	        }
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return pv;
    }
    // $ANTLR end "update_statement"



    // $ANTLR start "set_columns_clause"
    // D:\\TJSIB\\SQL.g:197:1: set_columns_clause returns [HashMap result] : SET_SYM name= set_column_clause ( COMMA name2= set_column_clause )* ;
    public final HashMap set_columns_clause() throws RecognitionException {
        HashMap result = null;


        OracleSQLParser.set_column_clause_return name =null;

        OracleSQLParser.set_column_clause_return name2 =null;


        try {
            // D:\\TJSIB\\SQL.g:198:2: ( SET_SYM name= set_column_clause ( COMMA name2= set_column_clause )* )
            // D:\\TJSIB\\SQL.g:198:4: SET_SYM name= set_column_clause ( COMMA name2= set_column_clause )*
            {
            match(input,SET_SYM,FOLLOW_SET_SYM_in_set_columns_clause1221); 

            pushFollow(FOLLOW_set_column_clause_in_set_columns_clause1225);
            name=set_column_clause();

            state._fsp--;


            result = new HashMap(); result.put((name!=null?name.key:null), (name!=null?name.value:null));

            // D:\\TJSIB\\SQL.g:199:2: ( COMMA name2= set_column_clause )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==COMMA) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // D:\\TJSIB\\SQL.g:199:4: COMMA name2= set_column_clause
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_set_columns_clause1232); 

            	    pushFollow(FOLLOW_set_column_clause_in_set_columns_clause1238);
            	    name2=set_column_clause();

            	    state._fsp--;


            	    result.put((name2!=null?name2.key:null), (name2!=null?name2.value:null));

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "set_columns_clause"


    public static class set_column_clause_return extends ParserRuleReturnScope {
        public String key;
        public String value;
    };


    // $ANTLR start "set_column_clause"
    // D:\\TJSIB\\SQL.g:204:1: set_column_clause returns [String key, String value] : ( ( column_name EQ_SYM literal_value ) | ( column_name IS_SYM NULL_SYM ) );
    public final OracleSQLParser.set_column_clause_return set_column_clause() throws RecognitionException {
        OracleSQLParser.set_column_clause_return retval = new OracleSQLParser.set_column_clause_return();
        retval.start = input.LT(1);


        String column_name9 =null;

        String literal_value10 =null;

        String column_name11 =null;


        try {
            // D:\\TJSIB\\SQL.g:205:2: ( ( column_name EQ_SYM literal_value ) | ( column_name IS_SYM NULL_SYM ) )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ID) ) {
                int LA5_1 = input.LA(2);

                if ( (LA5_1==EQ_SYM) ) {
                    alt5=1;
                }
                else if ( (LA5_1==IS_SYM) ) {
                    alt5=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // D:\\TJSIB\\SQL.g:205:4: ( column_name EQ_SYM literal_value )
                    {
                    // D:\\TJSIB\\SQL.g:205:4: ( column_name EQ_SYM literal_value )
                    // D:\\TJSIB\\SQL.g:205:5: column_name EQ_SYM literal_value
                    {
                    pushFollow(FOLLOW_column_name_in_set_column_clause1260);
                    column_name9=column_name();

                    state._fsp--;


                    match(input,EQ_SYM,FOLLOW_EQ_SYM_in_set_column_clause1262); 

                    pushFollow(FOLLOW_literal_value_in_set_column_clause1264);
                    literal_value10=literal_value();

                    state._fsp--;


                    }



                    		retval.key =column_name9;
                    		retval.value =literal_value10.trim().replaceAll("\'", "");
                    	

                    }
                    break;
                case 2 :
                    // D:\\TJSIB\\SQL.g:210:4: ( column_name IS_SYM NULL_SYM )
                    {
                    // D:\\TJSIB\\SQL.g:210:4: ( column_name IS_SYM NULL_SYM )
                    // D:\\TJSIB\\SQL.g:210:5: column_name IS_SYM NULL_SYM
                    {
                    pushFollow(FOLLOW_column_name_in_set_column_clause1274);
                    column_name11=column_name();

                    state._fsp--;


                    match(input,IS_SYM,FOLLOW_IS_SYM_in_set_column_clause1276); 

                    match(input,NULL_SYM,FOLLOW_NULL_SYM_in_set_column_clause1278); 

                    }


                    retval.key =column_name11; retval.value ="";

                    }
                    break;

            }
            retval.stop = input.LT(-1);


        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "set_column_clause"



    // $ANTLR start "where_clause"
    // D:\\TJSIB\\SQL.g:213:1: where_clause returns [HashMap result] : WHERE name= set_column_clause ( AND_SYM name2= set_column_clause )* ;
    public final HashMap where_clause() throws RecognitionException {
        HashMap result = null;


        OracleSQLParser.set_column_clause_return name =null;

        OracleSQLParser.set_column_clause_return name2 =null;


        try {
            // D:\\TJSIB\\SQL.g:214:2: ( WHERE name= set_column_clause ( AND_SYM name2= set_column_clause )* )
            // D:\\TJSIB\\SQL.g:215:2: WHERE name= set_column_clause ( AND_SYM name2= set_column_clause )*
            {
            match(input,WHERE,FOLLOW_WHERE_in_where_clause1295); 

            pushFollow(FOLLOW_set_column_clause_in_where_clause1299);
            name=set_column_clause();

            state._fsp--;


            result = new HashMap(); result.put((name!=null?name.key:null), (name!=null?name.value:null));

            // D:\\TJSIB\\SQL.g:216:2: ( AND_SYM name2= set_column_clause )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==AND_SYM) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // D:\\TJSIB\\SQL.g:216:4: AND_SYM name2= set_column_clause
            	    {
            	    match(input,AND_SYM,FOLLOW_AND_SYM_in_where_clause1306); 

            	    pushFollow(FOLLOW_set_column_clause_in_where_clause1311);
            	    name2=set_column_clause();

            	    state._fsp--;


            	    result.put((name2!=null?name2.key:null), (name2!=null?name2.value:null));

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "where_clause"



    // $ANTLR start "insert_statement"
    // D:\\TJSIB\\SQL.g:221:1: insert_statement returns [ParseredValue pv] : insert_header column_list value_list_clause ;
    public final ParseredValue insert_statement() throws RecognitionException {
        ParseredValue pv = null;


        String insert_header12 =null;

        ArrayList column_list13 =null;

        ArrayList value_list_clause14 =null;


        try {
            // D:\\TJSIB\\SQL.g:222:2: ( insert_header column_list value_list_clause )
            // D:\\TJSIB\\SQL.g:223:2: insert_header column_list value_list_clause
            {
            pushFollow(FOLLOW_insert_header_in_insert_statement1333);
            insert_header12=insert_header();

            state._fsp--;


            pushFollow(FOLLOW_column_list_in_insert_statement1336);
            column_list13=column_list();

            state._fsp--;


            pushFollow(FOLLOW_value_list_clause_in_insert_statement1339);
            value_list_clause14=value_list_clause();

            state._fsp--;



            		pv = new ParseredValue();
            		pv.setOp(MqConstant.INSERT);
            		String tableName = insert_header12;
                	  
                	  	if(!isValidTableName(tableName))
                			return null;
                		  
            		pv.setTableName(tableName);
            		
            		ArrayList keyArr = column_list13;
            		ArrayList valueArr = value_list_clause14;
            		
            		for (int i = 0; i < keyArr.size(); i++)
            		{
            			String key = keyArr.get(i).toString().trim();
            			String value = valueArr.get(i).toString().trim().replaceAll("\'", "");
            	    		pv.getValueMap().put(key, value);
                		}
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return pv;
    }
    // $ANTLR end "insert_statement"



    // $ANTLR start "insert_header"
    // D:\\TJSIB\\SQL.g:248:1: insert_header returns [String result] : INSERT INTO table_spec ;
    public final String insert_header() throws RecognitionException {
        String result = null;


        String table_spec15 =null;


        try {
            // D:\\TJSIB\\SQL.g:249:2: ( INSERT INTO table_spec )
            // D:\\TJSIB\\SQL.g:249:4: INSERT INTO table_spec
            {
            match(input,INSERT,FOLLOW_INSERT_in_insert_header1356); 

            match(input,INTO,FOLLOW_INTO_in_insert_header1358); 

            pushFollow(FOLLOW_table_spec_in_insert_header1360);
            table_spec15=table_spec();

            state._fsp--;



            		result = table_spec15;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "insert_header"



    // $ANTLR start "table_spec"
    // D:\\TJSIB\\SQL.g:255:1: table_spec returns [String result] : ( schema_name DOT )? table_name ;
    public final String table_spec() throws RecognitionException {
        String result = null;


        String table_name16 =null;


        try {
            // D:\\TJSIB\\SQL.g:256:2: ( ( schema_name DOT )? table_name )
            // D:\\TJSIB\\SQL.g:256:4: ( schema_name DOT )? table_name
            {
            // D:\\TJSIB\\SQL.g:256:4: ( schema_name DOT )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==DOT) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // D:\\TJSIB\\SQL.g:256:6: schema_name DOT
                    {
                    pushFollow(FOLLOW_schema_name_in_table_spec1379);
                    schema_name();

                    state._fsp--;


                    match(input,DOT,FOLLOW_DOT_in_table_spec1381); 

                    }
                    break;

            }


            pushFollow(FOLLOW_table_name_in_table_spec1386);
            table_name16=table_name();

            state._fsp--;



            		result = table_name16;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "table_spec"



    // $ANTLR start "column_list"
    // D:\\TJSIB\\SQL.g:262:1: column_list returns [ArrayList result] : LPAREN name= column_name ( COMMA name2= column_name )* RPAREN ;
    public final ArrayList column_list() throws RecognitionException {
        ArrayList result = null;


        String name =null;

        String name2 =null;


        try {
            // D:\\TJSIB\\SQL.g:263:2: ( LPAREN name= column_name ( COMMA name2= column_name )* RPAREN )
            // D:\\TJSIB\\SQL.g:264:2: LPAREN name= column_name ( COMMA name2= column_name )* RPAREN
            {
            match(input,LPAREN,FOLLOW_LPAREN_in_column_list1404); 

            pushFollow(FOLLOW_column_name_in_column_list1411);
            name=column_name();

            state._fsp--;


            result = new ArrayList();  result.add(name);

            // D:\\TJSIB\\SQL.g:266:4: ( COMMA name2= column_name )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==COMMA) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // D:\\TJSIB\\SQL.g:266:5: COMMA name2= column_name
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_column_list1419); 

            	    pushFollow(FOLLOW_column_name_in_column_list1423);
            	    name2=column_name();

            	    state._fsp--;


            	    result.add(name2);

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match(input,RPAREN,FOLLOW_RPAREN_in_column_list1430); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "column_list"



    // $ANTLR start "value_list_clause"
    // D:\\TJSIB\\SQL.g:270:1: value_list_clause returns [ArrayList result] : ( VALUES | VALUE_SYM ) column_value_list ;
    public final ArrayList value_list_clause() throws RecognitionException {
        ArrayList result = null;


        ArrayList column_value_list17 =null;


        try {
            // D:\\TJSIB\\SQL.g:270:45: ( ( VALUES | VALUE_SYM ) column_value_list )
            // D:\\TJSIB\\SQL.g:271:2: ( VALUES | VALUE_SYM ) column_value_list
            {
            if ( (input.LA(1) >= VALUES && input.LA(1) <= VALUE_SYM) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            pushFollow(FOLLOW_column_value_list_in_value_list_clause1451);
            column_value_list17=column_value_list();

            state._fsp--;



            		result = column_value_list17;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "value_list_clause"



    // $ANTLR start "column_value_list"
    // D:\\TJSIB\\SQL.g:277:1: column_value_list returns [ArrayList result] : LPAREN value= literal_value ( COMMA value2= literal_value )* RPAREN ;
    public final ArrayList column_value_list() throws RecognitionException {
        ArrayList result = null;


        String value =null;

        String value2 =null;


        try {
            // D:\\TJSIB\\SQL.g:278:2: ( LPAREN value= literal_value ( COMMA value2= literal_value )* RPAREN )
            // D:\\TJSIB\\SQL.g:279:2: LPAREN value= literal_value ( COMMA value2= literal_value )* RPAREN
            {
            match(input,LPAREN,FOLLOW_LPAREN_in_column_value_list1469); 

            pushFollow(FOLLOW_literal_value_in_column_value_list1475);
            value=literal_value();

            state._fsp--;


            result = new ArrayList(); result.add(value);

            // D:\\TJSIB\\SQL.g:281:3: ( COMMA value2= literal_value )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // D:\\TJSIB\\SQL.g:281:4: COMMA value2= literal_value
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_column_value_list1482); 

            	    pushFollow(FOLLOW_literal_value_in_column_value_list1486);
            	    value2=literal_value();

            	    state._fsp--;


            	    result.add(value2);

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            match(input,RPAREN,FOLLOW_RPAREN_in_column_value_list1494); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "column_value_list"



    // $ANTLR start "literal_value"
    // D:\\TJSIB\\SQL.g:285:1: literal_value returns [String result] : ( STRING | number_literal | HEX_DIGIT | boolean_literal | BIT_NUM | NULL_SYM | to_date ) ;
    public final String literal_value() throws RecognitionException {
        String result = null;


        Token STRING18=null;
        Token HEX_DIGIT20=null;
        Token BIT_NUM22=null;
        String number_literal19 =null;

        String boolean_literal21 =null;

        String to_date23 =null;


        try {
            // D:\\TJSIB\\SQL.g:286:2: ( ( STRING | number_literal | HEX_DIGIT | boolean_literal | BIT_NUM | NULL_SYM | to_date ) )
            // D:\\TJSIB\\SQL.g:287:9: ( STRING | number_literal | HEX_DIGIT | boolean_literal | BIT_NUM | NULL_SYM | to_date )
            {
            // D:\\TJSIB\\SQL.g:287:9: ( STRING | number_literal | HEX_DIGIT | boolean_literal | BIT_NUM | NULL_SYM | to_date )
            int alt10=7;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt10=1;
                }
                break;
            case FLOAT:
            case INT:
                {
                alt10=2;
                }
                break;
            case HEX_DIGIT:
                {
                alt10=3;
                }
                break;
            case FALSE_SYM:
            case TRUE_SYM:
                {
                alt10=4;
                }
                break;
            case BIT_NUM:
                {
                alt10=5;
                }
                break;
            case NULL_SYM:
                {
                alt10=6;
                }
                break;
            case TODATE:
                {
                alt10=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // D:\\TJSIB\\SQL.g:287:11: STRING
                    {
                    STRING18=(Token)match(input,STRING,FOLLOW_STRING_in_literal_value1518); 

                    result =(STRING18!=null?STRING18.getText():null);

                    }
                    break;
                case 2 :
                    // D:\\TJSIB\\SQL.g:288:12: number_literal
                    {
                    pushFollow(FOLLOW_number_literal_in_literal_value1533);
                    number_literal19=number_literal();

                    state._fsp--;


                    result =number_literal19;

                    }
                    break;
                case 3 :
                    // D:\\TJSIB\\SQL.g:289:12: HEX_DIGIT
                    {
                    HEX_DIGIT20=(Token)match(input,HEX_DIGIT,FOLLOW_HEX_DIGIT_in_literal_value1548); 

                    result =(HEX_DIGIT20!=null?HEX_DIGIT20.getText():null);

                    }
                    break;
                case 4 :
                    // D:\\TJSIB\\SQL.g:290:12: boolean_literal
                    {
                    pushFollow(FOLLOW_boolean_literal_in_literal_value1563);
                    boolean_literal21=boolean_literal();

                    state._fsp--;


                    result =boolean_literal21;

                    }
                    break;
                case 5 :
                    // D:\\TJSIB\\SQL.g:291:12: BIT_NUM
                    {
                    BIT_NUM22=(Token)match(input,BIT_NUM,FOLLOW_BIT_NUM_in_literal_value1578); 

                    result =(BIT_NUM22!=null?BIT_NUM22.getText():null);

                    }
                    break;
                case 6 :
                    // D:\\TJSIB\\SQL.g:292:12: NULL_SYM
                    {
                    match(input,NULL_SYM,FOLLOW_NULL_SYM_in_literal_value1593); 

                    result ="";

                    }
                    break;
                case 7 :
                    // D:\\TJSIB\\SQL.g:293:12: to_date
                    {
                    pushFollow(FOLLOW_to_date_in_literal_value1608);
                    to_date23=to_date();

                    state._fsp--;


                    result =to_date23;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "literal_value"



    // $ANTLR start "number_literal"
    // D:\\TJSIB\\SQL.g:297:1: number_literal returns [String result] : ( INT | FLOAT );
    public final String number_literal() throws RecognitionException {
        String result = null;


        Token INT24=null;
        Token FLOAT25=null;

        try {
            // D:\\TJSIB\\SQL.g:298:2: ( INT | FLOAT )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==INT) ) {
                alt11=1;
            }
            else if ( (LA11_0==FLOAT) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }
            switch (alt11) {
                case 1 :
                    // D:\\TJSIB\\SQL.g:298:4: INT
                    {
                    INT24=(Token)match(input,INT,FOLLOW_INT_in_number_literal1634); 

                    result =(INT24!=null?INT24.getText():null);

                    }
                    break;
                case 2 :
                    // D:\\TJSIB\\SQL.g:299:4: FLOAT
                    {
                    FLOAT25=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_number_literal1641); 

                    result =(FLOAT25!=null?FLOAT25.getText():null);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "number_literal"



    // $ANTLR start "boolean_literal"
    // D:\\TJSIB\\SQL.g:302:1: boolean_literal returns [String result] : ( TRUE_SYM | FALSE_SYM );
    public final String boolean_literal() throws RecognitionException {
        String result = null;


        Token TRUE_SYM26=null;
        Token FALSE_SYM27=null;

        try {
            // D:\\TJSIB\\SQL.g:303:2: ( TRUE_SYM | FALSE_SYM )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==TRUE_SYM) ) {
                alt12=1;
            }
            else if ( (LA12_0==FALSE_SYM) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }
            switch (alt12) {
                case 1 :
                    // D:\\TJSIB\\SQL.g:303:4: TRUE_SYM
                    {
                    TRUE_SYM26=(Token)match(input,TRUE_SYM,FOLLOW_TRUE_SYM_in_boolean_literal1657); 

                    result =(TRUE_SYM26!=null?TRUE_SYM26.getText():null);

                    }
                    break;
                case 2 :
                    // D:\\TJSIB\\SQL.g:304:4: FALSE_SYM
                    {
                    FALSE_SYM27=(Token)match(input,FALSE_SYM,FOLLOW_FALSE_SYM_in_boolean_literal1664); 

                    result =(FALSE_SYM27!=null?FALSE_SYM27.getText():null);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "boolean_literal"



    // $ANTLR start "schema_name"
    // D:\\TJSIB\\SQL.g:307:1: schema_name returns [String result] : ID ;
    public final String schema_name() throws RecognitionException {
        String result = null;


        Token ID28=null;

        try {
            // D:\\TJSIB\\SQL.g:308:3: ( ID )
            // D:\\TJSIB\\SQL.g:308:5: ID
            {
            ID28=(Token)match(input,ID,FOLLOW_ID_in_schema_name1681); 


            		result = (ID28!=null?ID28.getText():null);
            	 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "schema_name"



    // $ANTLR start "table_name"
    // D:\\TJSIB\\SQL.g:314:1: table_name returns [String result] : ID ;
    public final String table_name() throws RecognitionException {
        String result = null;


        Token ID29=null;

        try {
            // D:\\TJSIB\\SQL.g:315:2: ( ID )
            // D:\\TJSIB\\SQL.g:315:4: ID
            {
            ID29=(Token)match(input,ID,FOLLOW_ID_in_table_name1701); 


            		result = (ID29!=null?ID29.getText():null);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "table_name"



    // $ANTLR start "column_name"
    // D:\\TJSIB\\SQL.g:321:1: column_name returns [String result] : ID ;
    public final String column_name() throws RecognitionException {
        String result = null;


        Token ID30=null;

        try {
            // D:\\TJSIB\\SQL.g:322:2: ( ID )
            // D:\\TJSIB\\SQL.g:322:4: ID
            {
            ID30=(Token)match(input,ID,FOLLOW_ID_in_column_name1718); 


            		result = (ID30!=null?ID30.getText():null);
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "column_name"



    // $ANTLR start "to_date"
    // D:\\TJSIB\\SQL.g:328:1: to_date returns [String result] : TODATE LPAREN value= to_date_value COMMA to_data_format RPAREN ;
    public final String to_date() throws RecognitionException {
        String result = null;


        String value =null;


        try {
            // D:\\TJSIB\\SQL.g:329:2: ( TODATE LPAREN value= to_date_value COMMA to_data_format RPAREN )
            // D:\\TJSIB\\SQL.g:329:4: TODATE LPAREN value= to_date_value COMMA to_data_format RPAREN
            {
            match(input,TODATE,FOLLOW_TODATE_in_to_date1736); 

            match(input,LPAREN,FOLLOW_LPAREN_in_to_date1741); 

            pushFollow(FOLLOW_to_date_value_in_to_date1748);
            value=to_date_value();

            state._fsp--;


            result = value;

            match(input,COMMA,FOLLOW_COMMA_in_to_date1756); 

            pushFollow(FOLLOW_to_data_format_in_to_date1761);
            to_data_format();

            state._fsp--;


            match(input,RPAREN,FOLLOW_RPAREN_in_to_date1766); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "to_date"



    // $ANTLR start "to_date_value"
    // D:\\TJSIB\\SQL.g:337:1: to_date_value returns [String result] : STRING ;
    public final String to_date_value() throws RecognitionException {
        String result = null;


        Token STRING31=null;

        try {
            // D:\\TJSIB\\SQL.g:338:2: ( STRING )
            // D:\\TJSIB\\SQL.g:339:2: STRING
            {
            STRING31=(Token)match(input,STRING,FOLLOW_STRING_in_to_date_value1782); 

            result =(STRING31!=null?STRING31.getText():null);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "to_date_value"



    // $ANTLR start "to_data_format"
    // D:\\TJSIB\\SQL.g:342:1: to_data_format returns [String result] : STRING ;
    public final String to_data_format() throws RecognitionException {
        String result = null;


        Token STRING32=null;

        try {
            // D:\\TJSIB\\SQL.g:343:2: ( STRING )
            // D:\\TJSIB\\SQL.g:344:2: STRING
            {
            STRING32=(Token)match(input,STRING,FOLLOW_STRING_in_to_data_format1800); 

            result =(STRING32!=null?STRING32.getText():null);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return result;
    }
    // $ANTLR end "to_data_format"

    // Delegated rules


    protected DFA2 dfa2 = new DFA2(this);
    static final String DFA2_eotS =
        "\u0081\uffff";
    static final String DFA2_eofS =
        "\1\4\u0080\uffff";
    static final String DFA2_minS =
        "\1\12\1\33\1\30\1\22\1\uffff\1\30\1\13\1\30\1\13\2\30\1\13\2\30"+
        "\1\52\1\15\2\30\1\40\1\10\1\6\1\43\1\70\1\15\1\30\1\65\10\10\1\40"+
        "\1\10\1\6\1\43\1\10\1\40\2\30\1\53\10\uffff\1\40\1\uffff\1\6\2\15"+
        "\1\10\1\53\10\10\1\40\1\6\1\43\1\6\1\43\1\53\1\10\1\6\1\uffff\1"+
        "\53\10\10\1\40\1\10\10\uffff\1\40\1\uffff\1\50\1\53\10\10\1\40\1"+
        "\10\2\53\1\10\1\50\2\53\2\10\1\uffff\1\10\1\50\3\53\1\10\3\50\1"+
        "\10\1\uffff\1\10";
    static final String DFA2_maxS =
        "\1\63\1\33\1\30\1\22\1\uffff\1\30\1\52\1\30\1\40\2\30\1\70\2\30"+
        "\1\52\1\34\2\30\1\40\1\50\1\60\1\43\1\70\1\34\1\30\1\66\10\70\1"+
        "\40\1\70\1\60\1\43\1\50\1\40\2\30\1\53\10\uffff\1\40\1\uffff\1\60"+
        "\2\34\1\10\1\53\10\50\1\40\1\60\1\43\1\60\1\43\1\53\1\10\1\60\1"+
        "\uffff\1\53\10\70\1\40\1\70\10\uffff\1\40\1\uffff\1\50\1\53\10\50"+
        "\1\40\1\10\2\53\1\70\1\50\2\53\2\10\1\uffff\1\10\1\50\3\53\4\50"+
        "\1\70\1\uffff\1\50";
    static final String DFA2_acceptS =
        "\4\uffff\1\2\46\uffff\10\1\1\uffff\1\1\25\uffff\1\1\13\uffff\10"+
        "\1\1\uffff\1\1\24\uffff\1\1\12\uffff\1\1\1\uffff";
    static final String DFA2_specialS =
        "\u0081\uffff}>";
    static final String[] DFA2_transitionS = {
            "\1\3\16\uffff\1\1\31\uffff\1\2",
            "\1\5",
            "\1\6",
            "\1\7",
            "",
            "\1\10",
            "\1\11\36\uffff\1\12",
            "\1\13",
            "\1\14\24\uffff\1\15",
            "\1\16",
            "\1\17",
            "\1\20\54\uffff\1\21",
            "\1\22",
            "\1\23",
            "\1\12",
            "\1\24\16\uffff\1\25",
            "\1\26",
            "\1\27",
            "\1\15",
            "\1\30\37\uffff\1\31",
            "\1\40\11\uffff\1\37\1\34\3\uffff\1\35\4\uffff\1\33\10\uffff"+
            "\1\41\7\uffff\1\32\2\uffff\1\42\1\uffff\1\36",
            "\1\43",
            "\1\21",
            "\1\44\16\uffff\1\45",
            "\1\46",
            "\2\47",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\52",
            "\1\50\57\uffff\1\51",
            "\1\61\11\uffff\1\60\1\55\3\uffff\1\56\4\uffff\1\54\10\uffff"+
            "\1\62\7\uffff\1\53\2\uffff\1\63\1\uffff\1\57",
            "\1\64",
            "\1\30\37\uffff\1\31",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\71",
            "",
            "\1\100\11\uffff\1\77\1\74\3\uffff\1\75\4\uffff\1\73\10\uffff"+
            "\1\101\7\uffff\1\72\2\uffff\1\102\1\uffff\1\76",
            "\1\103\16\uffff\1\104",
            "\1\105\16\uffff\1\106",
            "\1\107",
            "\1\110",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\113",
            "\1\122\11\uffff\1\121\1\116\3\uffff\1\117\4\uffff\1\115\10"+
            "\uffff\1\123\7\uffff\1\114\2\uffff\1\124\1\uffff\1\120",
            "\1\125",
            "\1\134\11\uffff\1\133\1\130\3\uffff\1\131\4\uffff\1\127\10"+
            "\uffff\1\135\7\uffff\1\126\2\uffff\1\136\1\uffff\1\132",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\150\11\uffff\1\147\1\144\3\uffff\1\145\4\uffff\1\143\10"+
            "\uffff\1\151\7\uffff\1\142\2\uffff\1\152\1\uffff\1\146",
            "",
            "\1\153",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\50\57\uffff\1\51",
            "\1\154",
            "\1\50\57\uffff\1\51",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\155",
            "",
            "\1\156",
            "\1\157",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\111\37\uffff\1\112",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\50\57\uffff\1\51",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "\1\170",
            "",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "\1\111\37\uffff\1\112",
            "\1\176",
            "\1\177",
            "\1\u0080",
            "\1\50\57\uffff\1\51",
            "",
            "\1\111\37\uffff\1\112"
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "()* loopback of 138:2: (s2= sql_statement )*";
        }
    }
 

    public static final BitSet FOLLOW_parsed_one_package_in_parsed_value_list1066 = new BitSet(new long[]{0x0008000002000402L});
    public static final BitSet FOLLOW_parsed_one_package_in_parsed_value_list1081 = new BitSet(new long[]{0x0008000002000402L});
    public static final BitSet FOLLOW_sql_statement_in_parsed_one_package1110 = new BitSet(new long[]{0x0008000002000402L});
    public static final BitSet FOLLOW_sql_statement_in_parsed_one_package1119 = new BitSet(new long[]{0x0008000002000402L});
    public static final BitSet FOLLOW_insert_statement_in_sql_statement1139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_update_statement_in_sql_statement1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delete_statement_in_sql_statement1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_SYM_in_delete_statement1170 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_FROM_in_delete_statement1172 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_table_spec_in_delete_statement1174 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_where_clause_in_delete_statement1177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_update_statement1196 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_table_spec_in_update_statement1198 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_set_columns_clause_in_update_statement1201 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_where_clause_in_update_statement1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_SYM_in_set_columns_clause1221 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_set_column_clause_in_set_columns_clause1225 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_COMMA_in_set_columns_clause1232 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_set_column_clause_in_set_columns_clause1238 = new BitSet(new long[]{0x0000000000000102L});
    public static final BitSet FOLLOW_column_name_in_set_column_clause1260 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_EQ_SYM_in_set_column_clause1262 = new BitSet(new long[]{0x0001480804230040L});
    public static final BitSet FOLLOW_literal_value_in_set_column_clause1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_name_in_set_column_clause1274 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_IS_SYM_in_set_column_clause1276 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_NULL_SYM_in_set_column_clause1278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_where_clause1295 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_set_column_clause_in_where_clause1299 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_AND_SYM_in_where_clause1306 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_set_column_clause_in_where_clause1311 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_insert_header_in_insert_statement1333 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_column_list_in_insert_statement1336 = new BitSet(new long[]{0x0060000000000000L});
    public static final BitSet FOLLOW_value_list_clause_in_insert_statement1339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INSERT_in_insert_header1356 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_INTO_in_insert_header1358 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_table_spec_in_insert_header1360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_schema_name_in_table_spec1379 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_table_spec1381 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_table_name_in_table_spec1386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_column_list1404 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_column_name_in_column_list1411 = new BitSet(new long[]{0x0000010000000100L});
    public static final BitSet FOLLOW_COMMA_in_column_list1419 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_column_name_in_column_list1423 = new BitSet(new long[]{0x0000010000000100L});
    public static final BitSet FOLLOW_RPAREN_in_column_list1430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_value_list_clause1443 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_column_value_list_in_value_list_clause1451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_column_value_list1469 = new BitSet(new long[]{0x0001480804230040L});
    public static final BitSet FOLLOW_literal_value_in_column_value_list1475 = new BitSet(new long[]{0x0000010000000100L});
    public static final BitSet FOLLOW_COMMA_in_column_value_list1482 = new BitSet(new long[]{0x0001480804230040L});
    public static final BitSet FOLLOW_literal_value_in_column_value_list1486 = new BitSet(new long[]{0x0000010000000100L});
    public static final BitSet FOLLOW_RPAREN_in_column_value_list1494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_value1518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_literal_in_literal_value1533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_DIGIT_in_literal_value1548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_literal_in_literal_value1563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BIT_NUM_in_literal_value1578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_SYM_in_literal_value1593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_to_date_in_literal_value1608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_number_literal1634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_number_literal1641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_SYM_in_boolean_literal1657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_SYM_in_boolean_literal1664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_schema_name1681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_table_name1701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_column_name1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TODATE_in_to_date1736 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_LPAREN_in_to_date1741 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_to_date_value_in_to_date1748 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_COMMA_in_to_date1756 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_to_data_format_in_to_date1761 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_RPAREN_in_to_date1766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_to_date_value1782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_to_data_format1800 = new BitSet(new long[]{0x0000000000000002L});

}
