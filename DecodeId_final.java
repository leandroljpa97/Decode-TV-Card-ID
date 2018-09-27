/*----------------------------------------------------------------------------------------------------------------------------
The goal is to receive id_card and decoder the code in order to know all the products and their status
For example: If the user inputs the id: "70022180305000000201868920180910100480510000100040010007020004038444C101000702000400000298" the program break down the id into:

70022180305000000201868920180910 | 1004 805100001 00040 |010007020004038444C101000702000400000298   where:

70022180305000000201868920180910 (the first 32 digits, until reach the nr. 1004 ( command) , are the header) 

1004 (command) |805100001 (9 digits: transation nr)    | 00040 (allways 5 digits : contains the length of data -> next digit until the end of the string )

In this example, the length is '40' then 010007020004038444C101000702000400000298 has 40 digits

01 (tag of product1) 0007 02 0004(nr of bytes of product 1-> in this case 8 digits) 038444C1(product nr with 8 digits) || 01(tag product 2) 0007 02 0004(bytes of product 2) 00000298 (product nr)
------------------------------------------------------------------------------------------------------------------------------------- */
import com.google.gson.*;


import java.util.*;
import java.util.Scanner;



//structure that contains each product information
class productList{
    int tag_data;
    int data_length;
    int product_nr;
    String content;
}



public class DecodeId
{  
public static void main(String args[]){  

String id_card="";
 if(args.length == 1) {
           id_card = args[0];            ;
            System.out.println("A string colocada foi = " + id_card );
        } 
	  else {
         System.err.println("Missing Input: Please enter the code.");
		 System.exit(0);
      }


 

String n_transacao= id_card.substring(36,45);
System.out.println("o numero de transacao e "+ n_transacao);  

String length_data= id_card.substring(45,50);

String data= id_card.substring(50, 50+ Integer.parseInt(length_data)); 

//array of lists, where each position of array represents a product(or device) and the list contains the features of the current product.
//For example, if the data of id card is 01000B02000400004E2003000101 [WITOUT HEADER , COMMAND AND TRANSATION NUMBER]
//01 (tag) 000B (length of data) 02(tag) 0004(length) 00004E20(data) 03(tag) 0001(length) 01 (data)
//In this case we have only one product -> position '0' of array. 
//As we have 3 tags, there is 3 different information of the product, so that there is a list with 3 blocks, where each block represents this information 
//-> product properties | product id 20000 | product suspended
LinkedList<productList>[] product = (LinkedList<productList>[]) new LinkedList[100];

product= utils_func(data);
      System.out.println("JSON Object Array "+gson.toJson(product));




/*-----------------------------------------------------------------------------------------------------
                                 Print all information on output
--------------------------------------------------------------------------------------------------- */
int k=0;

while(product[k]!=null)
{
    k++;
System.out.println("--------------------------------");
System.out.println("Informação do produto/device nr "+ k);
System.out.println("--------------------------------");
k--;
for(int y=0;y<product[k].size();y++)
{
    System.out.println(product[k].get(y).content);
}
System.out.println("-------------------------------------");
k++;
}



}
//In this function the list (product) contains the information of all products
public static  LinkedList<productList>[] utils_func(String data){  
    
LinkedList<productList>[] product = (LinkedList<productList>[]) new LinkedList[100];
int append=0;
int length_product;
int initial_tag;
int i=0;

while(append<data.length())
{
    initial_tag= Integer.parseInt(data.substring(append,append+2));


length_product= Integer.parseInt(data.substring(append+2,append+6));

/*------------------------------------------------------------------------------
put all information in the list of the product
--------------------------------------------------------------------------------*/


if(product[i]==null)
    {
        product[i]=new LinkedList<productList>();
    }
    
    productList aux1= new productList();
    
    //save in the 1st block of the list the content of the information given by the 1st tag
    aux1.content=getContent(initial_tag,0);
    product[i].add(aux1);
    
    //it happens when the tag is 00 for example. In this case we have allways 00 (tag) 0001 (length) 01 -> device suspenser and there isn't more information about this device 
	//-> so there is only 1 block of the list
    if(length_product==1)
    {
        i++;
        append=append +6+ 2*length_product;
        continue;
    }

int j=0;
while(j<length_product)
{
     productList aux= new productList();
     
    int tag_prod=Integer.parseInt(data.substring(append+6+j,append+j+8));

    int length_prod=Integer.parseInt(data.substring(append+8+j,append+12+j));

    int id=Integer.parseInt(data.substring(append+12+j,append+12+j+(2*length_prod)),16);
    aux.tag_data= tag_prod;
    aux.data_length=length_prod;
    aux.product_nr=id;
    aux.content= getContent(tag_prod,id);
    

    product[i].add(aux);
    
    j=j+6+length_prod;
}
//---------------------------------------------------------------------
    
    i++;
    append=append +6+ 2*length_product;
    
    
}


return product;
}

//-----------------------------------------------------------------------------------------------------------------------------------------------------
//Eventually, in the future, if it is possible to get start/end date from the id card, have to append code (tag 4,5,6) to this function!
// Only put if(tag==5){s="start data is "+ "id"}, and we have to send the 'date' in 'id' to receive the 'date' in this function
//---------------------------------------------------------------------------------------------------------------------------------------------------
public static String getContent(int tag,int id){  
    String s="";
    if (tag==0)
    {
        s="Device suspended";
    }
    else if (tag==1)
    {
        s="Product Properties";
    }
    
    else if(tag==2)
    { 
        s="product id "+id;
    }
    
    else if(tag==3)
    {
        s="product suspended";
    }
    return s;


}

}