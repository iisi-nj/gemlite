package gemlite.core.util;

import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

@SuppressWarnings("rawtypes")
public class CommandBase
{
  protected TreeSet<CmdField> cmdFields;
  protected CmdLineParser parser;
  
  public void printMenu()
  {
    if (cmdFields == null)
      parse();
    StringBuffer buffer = new StringBuffer();
    for (CmdField f : cmdFields)
    {
      Object value = getValue(f);
      buffer.append(f.option.name()).append(" ").append(value).append(" [").append(f.option.usage()).append("]\n");
    }
    System.out.println(buffer.toString());
    
  }
  
  protected void printValue()
  {
    StringBuffer buffer = new StringBuffer();
    for (CmdField f : cmdFields)
    {
      Object value = getValue(f);
      // 如果输入为*,则置其值为""
      if ("*".equals(value.toString()))
      {
        setValue(f);
      }
      buffer.append(f.option.name()).append(" ").append(value).append(" ");
    }
    System.out.println(buffer.toString());
  }
  
  private final static String FLAG_WAIT_INPUT="Input your options,press Enter use last input,press X exit.";
  private final static String FLAG_ANY_KEY="Press any key to continue,press X exit.";
  protected boolean waitUserInput()
  {
    return waitUserInput(null,FLAG_WAIT_INPUT);
  }
  protected boolean waitAnyKey()
  {
    return waitUserInput(null,FLAG_ANY_KEY);
  }
  
  /***
   * 返回false,需要退出
   * 
   * @return
   * @throws CmdLineException
   */
  protected boolean waitUserInput(String[] args,String msg)
  {
    Scanner reader = new Scanner(System.in);
    
    boolean hasInitInput = args != null && args.length > 0;
    if(!hasInitInput)
      System.out.println(msg);
    do//while (hasInitInput && reader.hasNextLine())
    {
      String line = null;
      String[] newArgs = null;
      if (hasInitInput)
      {
        newArgs = args;
      }
      else
      {
        line = reader.nextLine();
        if (line.equalsIgnoreCase("X"))
        {
          reader.close();
          return false;
        }
        newArgs = line.split(" ");
      }
      try
      {
        if (newArgs.length > 0 && !newArgs[0].isEmpty())
          parser.parseArgument(newArgs);
        else
          System.err.println("please input your args!");
        printValue();
      }
      catch (CmdLineException e)
      {
        e.printStackTrace();
        System.out.println("Error, last Input:[" + line + "]\nInput:");
        continue;
      }
      break;
    }while(true);
    reader.close();
    return true;
  }
  
  protected String getValue(CmdField f)
  {
    Object value = null;
    f.field.setAccessible(true);
    try
    {
      value = f.field.get(this);
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    return StringUtils.trim("" + value);
  }
  
  /**
   * 设置值
   * 
   * @param f
   * @return
   */
  protected String setValue(CmdField f)
  {
    Object value = null;
    f.field.setAccessible(true);
    try
    {
      f.field.set(this, "");
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    return StringUtils.trim("" + value);
  }
  
  public void parse()
  {
    cmdFields = new TreeSet<CmdField>();
    for (Class c = getClass(); c != null; c = c.getSuperclass())
    {
      for (Field f : c.getDeclaredFields())
      {
        Option o = f.getAnnotation(Option.class);
        if (o != null)
        {
          CmdField cf = new CmdField();
          cf.field = f;
          cf.option = o;
          cmdFields.add(cf);
        }
      }
    }
  }
  
}

class CmdField implements Comparable<CmdField>
{
  public Field field;
  public Option option;
  
  @Override
  public int compareTo(CmdField o)
  {
    return this.field.getName().compareTo(o.field.getName());
  }
}
