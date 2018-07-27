using System;
using System.Collections.Generic;
using System.Linq;
using System.Data;
using System.Web;
using Newtonsoft.Json.Linq;
using System.Web.Services;
using System.Web.Script.Serialization;
using System.Data.SqlClient;
using System.Configuration;
/// <summary>
/// Summary description for WebService
/// </summary>
[WebService(Namespace = "http://tempuri.org/")]
[WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
// To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
// [System.Web.Script.Services.ScriptService]
public class WebService : System.Web.Services.WebService
{
    public WebService()
    {

        //Uncomment the following line if using designed components 
        //InitializeComponent(); 
    }

    [WebMethod]
    public string HelloWorld()
    {
        return "Hello World";
    }
    [WebMethod]
    public void viewAllItems()
    {
        //JObject jObject = JObject.Parse(str);

        string cs = "workstation id=storeinfo.mssql.somee.com;packet size=4096;user id=shivamkanodia_SQLLogin_1;pwd=u2dzhnhdnl;data source=storeinfo.mssql.somee.com;persist security info=False;initial catalog=storeinfo";
        SqlConnection con = new SqlConnection(cs);
        
        HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
        if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
        {
            //These headers are handling the "pre-flight" OPTIONS call sent by the browser
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
            HttpContext.Current.Response.End();
        }
        String resultJSON = "";
        DataTable dt;
        JavaScriptSerializer js = new JavaScriptSerializer();
        try
        {
            using (SqlCommand cmd = new SqlCommand("select * from BigBazaar;"))
            {
                cmd.Connection = con;
                using (SqlDataAdapter sda = new SqlDataAdapter(cmd))
                {
                     dt = new DataTable();
                    sda.Fill(dt);
                }
            }

            Context.Response.Clear();
            Context.Response.ContentType = "application/json";
            con.Open();
            con.Close();
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            List<Dictionary<String, Object>> tableRows = new List<Dictionary<string, object>>();
            Dictionary<String, Object> row;
            
            foreach (DataRow dr in dt.Rows)
            {
                row = new Dictionary<string, object>();

                foreach (DataColumn col in dt.Columns)
                {
                    row.Add(col.ColumnName, dr[col].ToString());
                }

                tableRows.Add(row);
            }
           resultJSON = serializer.Serialize(tableRows).ToString();
        }
        catch (Exception ex)
        {
            resultJSON = ex.Message.ToString();
        }
        Context.Response.Write(resultJSON);
    }

    [WebMethod]
    public void addStoreItems(string pn,string pt,int price,string bar,string md,string ed,int sn,int an,int pid,string desc)
    {
        string cs = "workstation id=storeinfo.mssql.somee.com;packet size=4096;user id=shivamkanodia_SQLLogin_1;pwd=u2dzhnhdnl;data source=storeinfo.mssql.somee.com;persist security info=False;initial catalog=storeinfo";
        SqlConnection con = new SqlConnection(cs);

        HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
        if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
        {
            //These headers are handling the "pre-flight" OPTIONS call sent by the browser
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
            HttpContext.Current.Response.End();
        }
        String resultJSON = "";
        JavaScriptSerializer js = new JavaScriptSerializer();
        try
        {
            SqlCommand cm = new SqlCommand("select ProductId from ProductId where ProductName='" + pn + "'");
            cm.Connection = con;
            con.Open();
            if (cm.ExecuteScalar()==null)
            addNewProduct(pn);
            using (SqlCommand cmd = new SqlCommand("select ProductId from ProductId where ProductName='"+pn+"'"))
            {
                cmd.Connection = con;
                pid = (int)cmd.ExecuteScalar();
            }
            using (SqlCommand cmd = new SqlCommand("INSERT INTO BigBazaar(ProductName,ProductType,Price,BarcodeNumber,ManufacturingDate,ExpiryDate,ShelfNumber,ProductId,Description) VALUES('"+pn+"','"+pt+"','"+price+"','"+bar+"','"+md+ "','" + ed + "','" + sn + "','" + pid + "','" + desc + "')"))
            {
                cmd.Connection = con;
                cmd.ExecuteNonQuery();
            }

            Context.Response.Clear();
            Context.Response.ContentType = "application/json";
            con.Close();
            
        }
        catch (Exception ex)
        {
            resultJSON = ex.Message.ToString();
        }
        Context.Response.Write(resultJSON);
    }
    public void addNewProduct(string str)
    {
        string cs = "workstation id=storeinfo.mssql.somee.com;packet size=4096;user id=shivamkanodia_SQLLogin_1;pwd=u2dzhnhdnl;data source=storeinfo.mssql.somee.com;persist security info=False;initial catalog=storeinfo";
        SqlConnection con = new SqlConnection(cs);

        HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
        if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
        {
            //These headers are handling the "pre-flight" OPTIONS call sent by the browser
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
            HttpContext.Current.Response.End();
        }
        String resultJSON = "";
        JavaScriptSerializer js = new JavaScriptSerializer();
        try
        {
            using (SqlCommand cmd = new SqlCommand("INSERT INTO ProductId(ProductName) VALUES('" + str + "')"))
            {
                con.Open();
                cmd.Connection = con;
                cmd.ExecuteNonQuery();
            }

            Context.Response.Clear();
            Context.Response.ContentType = "application/json";
            con.Close();

        }
        catch (Exception ex)
        {
            resultJSON = ex.Message.ToString();
        }
        Context.Response.Write(resultJSON);

    }
    [WebMethod]
    public void viewStockOfProduct(string str)
    {
        string cs = "workstation id=storeinfo.mssql.somee.com;packet size=4096;user id=shivamkanodia_SQLLogin_1;pwd=u2dzhnhdnl;data source=storeinfo.mssql.somee.com;persist security info=False;initial catalog=storeinfo";
        SqlConnection con = new SqlConnection(cs);

        HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
        if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
        {
            //These headers are handling the "pre-flight" OPTIONS call sent by the browser
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
            HttpContext.Current.Response.End();
        }
        String resultJSON = "";
        DataTable dt;
        JavaScriptSerializer js = new JavaScriptSerializer();
        try
        {
            using (SqlCommand cmd = new SqlCommand("select * from BigBazaar where Productname='"+str+"'"))
            {
                cmd.Connection = con;
                using (SqlDataAdapter sda = new SqlDataAdapter(cmd))
                {
                    dt = new DataTable();
                    sda.Fill(dt);
                }
            }

            Context.Response.Clear();
            Context.Response.ContentType = "application/json";
            con.Open();
            con.Close();
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            List<Dictionary<String, Object>> tableRows = new List<Dictionary<string, object>>();
            Dictionary<String, Object> row;
            int i = 0;
            foreach (DataRow dr in dt.Rows)
            {
                row = new Dictionary<string, object>();
                i++;
                foreach (DataColumn col in dt.Columns)
                {
                    row.Add(col.ColumnName, dr[col].ToString());
                }

                tableRows.Add(row);
            }
            row = new Dictionary<string, object>();
            row.Add("No of Item", i);
            tableRows.Add(row);
            resultJSON = serializer.Serialize(tableRows).ToString();
        }
        catch (Exception ex)
        {
            resultJSON = ex.Message.ToString();
        }
        Context.Response.Write(resultJSON);

    }
    [WebMethod]
    public void getProductdetails(string str)
    {
        string cs = "workstation id=storeinfo.mssql.somee.com;packet size=4096;user id=shivamkanodia_SQLLogin_1;pwd=u2dzhnhdnl;data source=storeinfo.mssql.somee.com;persist security info=False;initial catalog=storeinfo";
        SqlConnection con = new SqlConnection(cs);

        HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
        if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
        {
            //These headers are handling the "pre-flight" OPTIONS call sent by the browser
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
            HttpContext.Current.Response.End();
        }
        String resultJSON = "";
        DataTable dt;
        JavaScriptSerializer js = new JavaScriptSerializer();
        try
        {
            using (SqlCommand cmd = new SqlCommand("select * from BigBazaar where BarcodeNumber='" + str + "'"))
            {
                cmd.Connection = con;
                using (SqlDataAdapter sda = new SqlDataAdapter(cmd))
                {
                    dt = new DataTable();
                    sda.Fill(dt);
                }
            }

            Context.Response.Clear();
            Context.Response.ContentType = "application/json";
            con.Open();
            con.Close();
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            List<Dictionary<String, Object>> tableRows = new List<Dictionary<string, object>>();
            Dictionary<String, Object> row;
            int i = 0;
            foreach (DataRow dr in dt.Rows)
            {
                row = new Dictionary<string, object>();
                i++;
                foreach (DataColumn col in dt.Columns)
                {
                    row.Add(col.ColumnName, dr[col].ToString());
                }

                tableRows.Add(row);
            }
            resultJSON = serializer.Serialize(tableRows).ToString();
        }
        catch (Exception ex)
        {
            resultJSON = ex.Message.ToString();
        }
        Context.Response.Write(resultJSON);

    }


 [WebMethod]
    public void searchProduct(string str)
    {
        string cs = "workstation id=storeinfo.mssql.somee.com;packet size=4096;user id=shivamkanodia_SQLLogin_1;pwd=u2dzhnhdnl;data source=storeinfo.mssql.somee.com;persist security info=False;initial catalog=storeinfo";
        SqlConnection con = new SqlConnection(cs);

        HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
        if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
        {
            //These headers are handling the "pre-flight" OPTIONS call sent by the browser
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
            HttpContext.Current.Response.End();
        }
        String resultJSON = "";
        DataTable dt;
        JavaScriptSerializer js = new JavaScriptSerializer();
        try
        {
            using (SqlCommand cmd = new SqlCommand("select top(1) ProductName, ProductType, ShelfNumber, AisleNumber from BigBazaar where ProductId in (select ProductId from ProductId where ProductName='" + str + "')"))
            {
                cmd.Connection = con;
                using (SqlDataAdapter sda = new SqlDataAdapter(cmd))
                {
                    dt = new DataTable();
                    sda.Fill(dt);
                }
            }

            Context.Response.Clear();
            Context.Response.ContentType = "application/json";
            con.Open();
            con.Close();
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            List<Dictionary<String, Object>> tableRows = new List<Dictionary<string, object>>();
            Dictionary<String, Object> row;
            int i = 0;
            foreach (DataRow dr in dt.Rows)
            {
                row = new Dictionary<string, object>();
                i++;
                foreach (DataColumn col in dt.Columns)
                {
                    row.Add(col.ColumnName, dr[col].ToString());
                }

                tableRows.Add(row);
            }
            resultJSON = serializer.Serialize(tableRows).ToString();
        }
        catch (Exception ex)
        {
            resultJSON = ex.Message.ToString();
        }
        Context.Response.Write(resultJSON);

    }


    [WebMethod]
    public void deleteStockOfProduct(string ProductName,Int16 noOfItems)
    {
        string cs = "workstation id=storeinfo.mssql.somee.com;packet size=4096;user id=shivamkanodia_SQLLogin_1;pwd=u2dzhnhdnl;data source=storeinfo.mssql.somee.com;persist security info=False;initial catalog=storeinfo";
        SqlConnection con = new SqlConnection(cs);

        HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
        if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
        {
            //These headers are handling the "pre-flight" OPTIONS call sent by the browser
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
            HttpContext.Current.Response.End();
        }
        String resultJSON = "";
        DataTable dt;
        JavaScriptSerializer js = new JavaScriptSerializer();
        try
        {
            using (SqlCommand cmd = new SqlCommand("delete top("+noOfItems+") from BigBazaar where ProductName='"+ProductName+"'"))
            {
                cmd.Connection = con;
                con.Open();
                cmd.ExecuteNonQuery();
            }
            using (SqlCommand cmd = new SqlCommand("select (select count(*) from BigBazaar where ProductName='"+ProductName+"')"))
            {
                cmd.Connection = con;
                int c=(int)cmd.ExecuteScalar();
                if (c == 0)
                {
                    SqlCommand cm = new SqlCommand("delete * from ProductId where Productname='"+ProductName+"'");
                    cm.ExecuteScalar();
                }
            }

            Context.Response.Clear();
            Context.Response.ContentType = "application/json";
            con.Close();
        }
        catch (Exception ex)
        {
            resultJSON = ex.Message.ToString();
        }
        Context.Response.Write(resultJSON);

    }


}
