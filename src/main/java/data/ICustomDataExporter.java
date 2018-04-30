package data;

public interface ICustomDataExporter extends IDataExporter{
    public void addDataToOutputContainer(String data) throws Exception;
}
