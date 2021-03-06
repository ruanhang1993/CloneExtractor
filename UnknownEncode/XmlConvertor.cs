//==============================================================================
//  CHINA FOREIGN EXCHANGE TRADE SYSTEM
//  NEW TRADE SYSTEM
//==============================================================================
//  File:       XmlConvertor.cs
//  Summary:    this class define fuction for xml serialization and deserialization.
//
//--  Change History: ----------------------------------------------------------
//  When        Who         Why
//------------------------------------------------------------------------------
//  2010/03/31  Rob.Xie   Created. 
//==============================================================================

#region Refrences
using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

namespace CFETS.NGCNYTS.AutoUpdate.Common
{
    /// <summary>
    /// this class define fuction for xml serialization and deserialization.
    /// It can be used in the client object conversion.
    /// </summary>
    public static class XmlConvertor
    {
        private const string CONVERT_EXCEPTION_MSG = "Can not convert XML to Object. ";

        /// <summary>
        /// serialize an object to String.
        /// </summary>
        /// <param name="obj">the object.</param>
        /// <returns>the serialized String.</returns>
        public static string ObjectToXml( object obj )
        {
            return ObjectToXml( obj, true );
        }

        /// <summary>
        /// serialize an object to String.
        /// </summary>
        /// <param name="obj">the object.</param>
        /// <param name="toBeIndented">whether to be indented.</param>
        /// <returns>the serialized String.</returns>
        public static string ObjectToXml( object obj, bool toBeIndented )
        {
            if ( obj == null )
                throw new ArgumentNullException( "obj" );

            UTF8Encoding encoding1 = new UTF8Encoding( false );
            XmlSerializer serializer1 = new XmlSerializer( obj.GetType() );
            MemoryStream stream1 = new MemoryStream();
            XmlTextWriter writer1 = new XmlTextWriter( stream1, encoding1 );
            writer1.Formatting = toBeIndented ? Formatting.Indented : Formatting.None;
            serializer1.Serialize( writer1, obj );
            string text1 = encoding1.GetString( stream1.ToArray() );
            writer1.Close();
            return text1;
        }

        /// <summary>
        /// serialize an object to string using binary format.
        /// </summary>
        /// <param name="obj">the object.</param>
        /// <returns>the serialized String.</returns>
        public static string ObjectToXmlBin( object obj )
        {
            if ( obj == null )
            {
                throw new ArgumentNullException( "obj" );
            }
            BinaryFormatter formatter1 = new BinaryFormatter();
            byte[] bytes = null;
            using ( MemoryStream stream1 = new MemoryStream() )
            {
                formatter1.Serialize( stream1, obj );
                bytes = stream1.ToArray();
            }
            return Convert.ToBase64String( bytes );
        }

        /// <summary>
        /// deserialize String.to an object.
        /// </summary>
        /// <param name="xml">the string need to be deserialized.</param>
        /// <returns>the deserialized object.</returns>
        public static object XmlToObjectBin( string xml )
        {
            if ( String.IsNullOrEmpty( xml ) )
            {
                throw new ArgumentNullException( "xml" );
            }
            byte[] bytes = Convert.FromBase64String( xml );

            object obj = null;
            BinaryFormatter formatter1 = new BinaryFormatter();
            using ( MemoryStream stream1 = new MemoryStream() )
            {
                stream1.Write( bytes, 0, bytes.Length );
                stream1.Position = 0;
                obj = formatter1.Deserialize( stream1 );
            }
            return obj;
        }

        /// <summary>
        /// deserialize String.to an object.
        /// </summary>
        /// <param name="xml">the string need to be deserialized.</param>
        /// <param name="type">the type of the object.</param>
        /// <returns>the deserialized object.</returns>
        public static object XmlToObject( string xml, Type type )
        {
            if ( xml == null )
                throw new ArgumentNullException( "xml" );
            if ( type == null )
                throw new ArgumentNullException( "type" );

            object result = null;
            XmlSerializer serializer = new XmlSerializer( type );
            using ( StringReader stringReader = new StringReader( xml ) )
            {
                using ( XmlReader xmlReader = new XmlTextReader( stringReader ) )
                {
                    try
                    {
                        result = serializer.Deserialize( xmlReader );
                    }
                    catch ( InvalidOperationException ex )
                    {
                        throw new InvalidOperationException( CONVERT_EXCEPTION_MSG, ex );
                    }
                }
            }
            return result;
        }

        // XML -> OBJECT
        /// <summary>
        /// 从XML文件中读入成指定类型的对象
        /// </summary>
        /// <param name="filePath"></param>
        /// <param name="type"></param>
        /// <returns></returns>
        public static object LoadXml( string filePath, Type type )
        {
            if ( !File.Exists( filePath ) )
                return null;
            using ( StreamReader reader = new StreamReader( filePath ) )
            {
                XmlSerializer xs = new XmlSerializer( type );
                object obj = xs.Deserialize( reader );
                reader.Close();
                return obj;
            }
        }

        /// <summary>
        /// 从XML文件中读入成泛型参数类型的对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="filePath"></param>
        /// <returns></returns>
        public static T LoadXml<T>( string filePath ) where T : class
        {
            object result = LoadXml( filePath, typeof( T ) );
            return result as T;
        }

        /// <summary>
        /// 返回 XML 文本的友好打印格式字符串。
        /// </summary>
        /// <param name="xmlText"></param>
        /// <returns></returns>
        public static string PrettyPrint( string xmlText )
        {
            string result = String.Empty;
            try
            {
                using ( MemoryStream ms = new MemoryStream() )
                {
                    using ( XmlTextWriter w = new XmlTextWriter( ms, Encoding.Unicode ) )
                    {
                        XmlDocument d = new XmlDocument();
                        d.LoadXml( xmlText );
                        w.Formatting = Formatting.Indented;
                        d.WriteContentTo( w );
                        w.Flush();
                        ms.Flush();
                        ms.Position = 0;
                        using ( StreamReader sr = new StreamReader( ms ) )
                        {
                            String formattedXML = sr.ReadToEnd();
                            result = formattedXML;
                            sr.Close();
                        }
                        w.Close();
                    }
                    ms.Close();
                }
            }
            catch ( Exception ex )
            {
                ex.Data.Add( "xml Input", xmlText );
                throw;
            }

            return result;
        }

    }
}
