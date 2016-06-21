package som.apidiscoverer.bean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.mashape.unirest.http.exceptions.UnirestException;
import core.APIParameter;
import core.Api;
import core.JsonDataType;
import core.Path;
import core.Schema;
import som.apidiscoverer.Discoverer;
import som.apidiscoverer.Generator;
import som.apidiscoverer.model.APIRequest;
import som.apidiscoverer.model.HttpMethod;
import som.apidiscoverer.model.Response;
import som.apidiscoverer.model.TreeNodeEntry;
import som.apidiscoverer.util.RESTClient;

@ManagedBean(name = "discovererBean")
@ViewScoped
public class DiscovererBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<APIRequest> records;

	private APIRequest newAPIRequest;
	private Response response;
	private Discoverer discoverer;
	private DefaultStreamedContent download;
	private DefaultStreamedContent downloadAdvanced;
	private TreeNode apiTree;

	@PostConstruct
	public void init() {
		newAPIRequest = new APIRequest();
		APIRequest temp = new APIRequest();
		try {
			temp.setUrl("http://petstore.swagger.io/v2/pet");
		} catch (MalformedURLException e) {
		}
		temp.setHttpMethod(HttpMethod.POST);
		temp.setBody("{\"id\": 123,\"category\": {\"id\": 1,\"name\": \"dogs\"},\"name\": \"doggie\",\"photoUrls\": [\"http://example.com\"],\"tags\": [{\"id\": 1,\"name\": \"black\"}],\"status\": \"available\"}");
		try {
			RESTClient.send(temp);
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response = new Response();
		discoverer = new Discoverer();
		setRecords(new ArrayList<>());
	}

	public void setDownload(DefaultStreamedContent download) {
		this.download = download;
	}

	public DefaultStreamedContent getDownload() throws Exception {
		System.out.println("GET = " + download.getName());
		return download;
	}

	public TreeNode createTreeTable() {
		Api api = discoverer.getApi();

		TreeNode apiNode = new DefaultTreeNode(new TreeNodeEntry("OpenAPI", "-"), null);
		TreeNode hostNode = new DefaultTreeNode(new TreeNodeEntry("host", api.getHost()), apiNode);
		TreeNode basePathNode = new DefaultTreeNode(new TreeNodeEntry("basePath", api.getBasePath()), apiNode);

		// paths
		TreeNode pathsNode = new DefaultTreeNode(new TreeNodeEntry("paths", "-"), apiNode);
		for (Path path : api.getPaths()) {
			TreeNode pathNode = new DefaultTreeNode(new TreeNodeEntry(path.getPattern(), "-"), pathsNode);
			TreeNode operationsNode = new DefaultTreeNode(new TreeNodeEntry("operations", "-"), pathNode);
			if (path.getGet() != null) {

				TreeNode operationGetNode = new DefaultTreeNode(new TreeNodeEntry("get", "-"), operationsNode);
				TreeNode parametersNode = new DefaultTreeNode(new TreeNodeEntry("parameters", "-"), operationGetNode);
				for (APIParameter parameter : path.getGet().getParameters()) {
					TreeNode parameterNode = new DefaultTreeNode(
							new TreeNodeEntry(parameter.getName(), parameter.getIn().getLiteral()), parametersNode);
				}
				TreeNode responsesNode = new DefaultTreeNode(new TreeNodeEntry("responses", "-"), operationGetNode);
				for (core.Response response : path.getGet().getResponses()) {
					TreeNode responseNode = new DefaultTreeNode(new TreeNodeEntry(response.getCode(), "-"),
							responsesNode);
					if(response.getSchema()!=null){
						TreeNode schemaNode = new DefaultTreeNode(new TreeNodeEntry("schema", response.getSchema().getName()),
								responseNode);
						dispalaySchema(response.getSchema(), schemaNode);
					}

				}
			}
			if (path.getPost() != null) {
				TreeNode operationPostNode = new DefaultTreeNode(new TreeNodeEntry("post", "-"), operationsNode);
				TreeNode parametersNode = new DefaultTreeNode(new TreeNodeEntry("parameters", "-"), operationPostNode);
				for (APIParameter parameter : path.getPost().getParameters()) {
					TreeNode parameterNode = new DefaultTreeNode(
							new TreeNodeEntry(parameter.getName(), parameter.getIn().getLiteral()), parametersNode);
				}
				TreeNode responsesNode = new DefaultTreeNode(new TreeNodeEntry("responses", "-"), operationPostNode);
				for (core.Response response : path.getPost().getResponses()) {
					TreeNode responseNode = new DefaultTreeNode(new TreeNodeEntry(response.getCode(), "-"),
							responsesNode);
//					displayResponse(response,responseNode);
					if(response.getSchema()!=null){
						TreeNode schemaNode = new DefaultTreeNode(new TreeNodeEntry("schema", response.getSchema().getName()),
								responseNode);
						dispalaySchema(response.getSchema(), schemaNode);
					}

				}

			}
			if (path.getPut() != null) {
				TreeNode operationPutNode = new DefaultTreeNode(new TreeNodeEntry("put", "-"), operationsNode);
				TreeNode parametersNode = new DefaultTreeNode(new TreeNodeEntry("parameters", "-"), operationPutNode);
				for (APIParameter parameter : path.getPut().getParameters()) {
					TreeNode parameterNode = new DefaultTreeNode(
							new TreeNodeEntry(parameter.getName(), parameter.getIn().getLiteral()), parametersNode);
				}
				TreeNode responsesNode = new DefaultTreeNode(new TreeNodeEntry("responses", "-"), operationPutNode);
				for (core.Response response : path.getPut().getResponses()) {
					TreeNode responseNode = new DefaultTreeNode(new TreeNodeEntry(response.getCode(), "-"),
							responsesNode);
					if(response.getSchema()!=null){
						TreeNode schemaNode = new DefaultTreeNode(new TreeNodeEntry("schema", response.getSchema().getName()),
								responseNode);
						dispalaySchema(response.getSchema(), schemaNode);
					}

				}

			}
			if (path.getDelete() != null) {
				TreeNode operationDeletetNode = new DefaultTreeNode(new TreeNodeEntry("delete", "-"), operationsNode);
				TreeNode parametersNode = new DefaultTreeNode(new TreeNodeEntry("parameters", "-"),
						operationDeletetNode);
				for (APIParameter parameter : path.getDelete().getParameters()) {
					TreeNode parameterNode = new DefaultTreeNode(
							new TreeNodeEntry(parameter.getName(), parameter.getIn().getLiteral()), parametersNode);
				}
				TreeNode responsesNode = new DefaultTreeNode(new TreeNodeEntry("responses", "-"), operationDeletetNode);
				for (core.Response response : path.getDelete().getResponses()) {
					TreeNode responseNode = new DefaultTreeNode(new TreeNodeEntry(response.getCode(), "-"),
							responsesNode);
					if(response.getSchema()!=null){
						TreeNode schemaNode = new DefaultTreeNode(new TreeNodeEntry("schema", response.getSchema().getName()),
								responseNode);
						dispalaySchema(response.getSchema(), schemaNode);
					}
						

				}

			}

		}
		// definitions
		TreeNode definitionsNode = new DefaultTreeNode(new TreeNodeEntry("definitions", "-"), apiNode);
		for (Schema schema : api.getDefinitions()) {
			TreeNode schemaNode = new DefaultTreeNode(new TreeNodeEntry(schema.getName(), "-"), definitionsNode);
		dispalaySchema(schema,schemaNode);
		}
		return apiNode;

	}

	private void dispalaySchema(Schema schema, TreeNode schemaNode) {
		if(schema.getProperties()!=null){
		TreeNode propertiesNode = new DefaultTreeNode(new TreeNodeEntry("properties", "-"), schemaNode);
		
		for(Schema property :schema.getProperties()){
		diplayProperty(property,propertiesNode);
		
		}}
		if(schema.getItems() != null){
			TreeNode itemsNode = new DefaultTreeNode(new TreeNodeEntry("items", "-"), schemaNode);
			if(schema.getItems().getType().equals(JsonDataType.OBJECT)){
					TreeNode itemNode = new DefaultTreeNode(new TreeNodeEntry(schema.getItems().getName(), "-"), itemsNode);
			}
			else {
				TreeNode itemNode = new DefaultTreeNode(new TreeNodeEntry(schema.getItems().getType().getLiteral(), "-"), itemsNode);
				
			}
			
		}
	}

	private void diplayProperty(Schema property, TreeNode propertiesNode) {
		if(property.getType().equals(JsonDataType.OBJECT))
		{
			TreeNode propertyNode = new DefaultTreeNode(new TreeNodeEntry(property.getName(), property.getRefResolved().getName()), propertiesNode);
			
		}else {
			if(property.getType().equals(JsonDataType.ARRAY)){
				TreeNode arrayNode = new DefaultTreeNode(new TreeNodeEntry(property.getName(), JsonDataType.ARRAY.getLiteral()), propertiesNode);
				TreeNode itemsNode = new DefaultTreeNode(new TreeNodeEntry("items", "-"), arrayNode);
				if(property.getItems().getType().equals(JsonDataType.OBJECT)){
						TreeNode itemNode = new DefaultTreeNode(new TreeNodeEntry(property.getItems().getName(), "-"), itemsNode);
				}
				else {
					TreeNode itemNode = new DefaultTreeNode(new TreeNodeEntry(property.getItems().getType().getLiteral(), "-"), itemsNode);
					
				}
				
				
				
			}
			else {
				TreeNode propertyNode = new DefaultTreeNode(new TreeNodeEntry(property.getName(), property.getType().getLiteral()), propertiesNode);
				
			}
		}
		
	}

	private void displayResponse(core.Response response, TreeNode responseNode) {
if(response.getSchema()!=null){
	TreeNode schemaNode= new DefaultTreeNode(new TreeNodeEntry("schema",response.getSchema().getName()), responseNode);
	
	dispalaySchema(response.getSchema(), schemaNode);
}
	}

	public void prepDownload() throws Exception {
		File temp = File.createTempFile("swagger", ".json");
		FileWriter fileWritter = new FileWriter(temp.getPath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		System.out.println(Generator.getJsonFromSwaggerModel(discoverer.getApi()).toString());
		bufferWritter.write(Generator.getJsonFromSwaggerModel(discoverer.getApi()).toString());
		bufferWritter.close();
		InputStream input = new FileInputStream(temp);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		setDownload(new DefaultStreamedContent(input, externalContext.getMimeType(temp.getName()), temp.getName()));
	}
	public void prepDownloadAdvanced() throws Exception {
		discoverer.applyAdvancedHeurisitics();
		File temp = File.createTempFile("swagger", ".json");
		FileWriter fileWritter = new FileWriter(temp.getPath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(Generator.getJsonFromSwaggerModel(discoverer.getApiAdvenced()).toString());
		bufferWritter.close();
		InputStream input = new FileInputStream(temp);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		setDownloadAdvanced(new DefaultStreamedContent(input, externalContext.getMimeType(temp.getName()), temp.getName()));
	}

	public void sendRequest() throws MalformedURLException, URISyntaxException {
		try {
			newAPIRequest.decode();
			response = RESTClient.send(newAPIRequest);
			newAPIRequest.setResponse(response);
			discoverer.discover(newAPIRequest);
			records.add(newAPIRequest);
			newAPIRequest = new APIRequest();
			// response = new Response();

		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void cleanForms() {
		newAPIRequest = new APIRequest();
		response = new Response();

	}

	public List<APIRequest> getRecords() {
		return records;
	}

	public void setRecords(List<APIRequest> records) {
		this.records = records;
	}

	public APIRequest getNewAPIRequest() {
		return newAPIRequest;
	}

	public void setNewAPIRequest(APIRequest newAPIRequest) {
		this.newAPIRequest = newAPIRequest;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}



	public TreeNode getApiTree() {
		return apiTree;
	}

	public void setApiTree(TreeNode apiTree) {
		this.apiTree = apiTree;
	}

	public DefaultStreamedContent getDownloadAdvanced() {
		return downloadAdvanced;
	}

	public void setDownloadAdvanced(DefaultStreamedContent downloadAdvanced) {
		this.downloadAdvanced = downloadAdvanced;
	}

}