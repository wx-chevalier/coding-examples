public class Main {

    public static void main(String[] args) {
    
        String str1 = "reverse this string";
    
        Stack<Object> stack = new Stack<>();
    
        StringTokenizer strTok = new StringTokenizer(str1);
    
        while(strTok.hasMoreTokens()){
    
            stack.push(strTok.nextElement());
        }
    
        StringBuffer str1rev = new StringBuffer();
    
        while(!stack.empty()){
    
            str1rev.append(stack.pop());
            str1rev.append(" ");
    
    
        }
    
        System.out.println(str1rev);
    
    
    
    }
    }