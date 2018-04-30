package data;

public abstract interface IDataExporter {
    public void createOutputContainer() throws Exception;
    public void finishDataExport() throws Exception;
}
