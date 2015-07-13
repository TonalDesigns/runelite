package info.sigterm.deob.attributes.code;

import info.sigterm.deob.ClassFile;
import info.sigterm.deob.ConstantPool;
import info.sigterm.deob.pool.Class;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Exception
{
	private Exceptions exceptions;

	private Instruction start, end, handler;
	private Class catchType;

	public Exception(Exceptions exceptions) throws IOException
	{
		this.exceptions = exceptions;

		DataInputStream is = exceptions.getCode().getAttributes().getStream();
		ConstantPool pool = exceptions.getCode().getAttributes().getClassFile().getPool();

		int startPc = is.readUnsignedShort();
		int endPc = is.readUnsignedShort();
		int handlerPc = is.readUnsignedShort();
		catchType = pool.getClass(is.readUnsignedShort());
		
		Instructions instructions = exceptions.getCode().getInstructions();
		start = instructions.findInstruction(startPc);
		end = instructions.findInstruction(endPc);
		handler = instructions.findInstruction(handlerPc);
		
		assert start != null;
		assert end != null;
		assert handler != null;
	}
	
	public void write(DataOutputStream out) throws IOException
	{
		ConstantPool pool = exceptions.getCode().getAttributes().getClassFile().getPool();
		
		out.writeShort(start.getPc());
		out.writeShort(end.getPc());
		out.writeShort(handler.getPc());
		out.writeShort(catchType == null ? 0 : pool.make(catchType));
	}
	
	public Exceptions getExceptions()
	{
		return exceptions;
	}
	
	public Instruction getStart()
	{
		return start;
	}
	
	public Instruction getEnd()
	{
		return end;
	}
	
	public Instruction getHandler()
	{
		return handler;
	}
	
	public void replace(Instruction oldi, Instruction newi)
	{
		if (start == oldi)
			start = newi;
		
		if (end == oldi)
			end = newi;
		
		if (handler == oldi)
			handler = newi;
	}
	
	public Class getCatchType()
	{
		return catchType;
	}
	
	public void renameClass(ClassFile cf, String name)
	{
		if (catchType != null && cf.getName().equals(catchType.getName()))
			catchType = new Class(name);
	}
}
