//==============================================================================
//  CHINA FOREIGN EXCHANGE TRADE SYSTEM
//  NEW TRADE SYSTEM
//==============================================================================
//  File:		FileManager.cs
//  Summary:   	文件管理类,负责实现与文件相关的操作
//
//--  Change History: ----------------------------------------------------------
//  When        Who         Why
//------------------------------------------------------------------------------
//  2010/03/30  Rob.Xie     Created. 
//==============================================================================

#region Refrences
using System;
using System.IO;
using System.Reflection;
using System.Text;
using System.Threading;

#endregion

namespace CFETS.NGCNYTS.AutoUpdate.Common
{
    /// <summary>
    /// 文件管理类
    /// </summary>
    public static class FileManager
    {
        // 解决在 Web 站点模式下，GetEntryAssembly会返回null的问题
        //static string currentPath = Path.GetDirectoryName( Assembly.GetEntryAssembly().Location );
        static string currentPath = Path.GetDirectoryName( Assembly.GetExecutingAssembly().Location );

        /// <summary>
        /// 获取指定路径下的文件
        /// </summary>
        /// <param name="directPath"></param>
        /// <param name="containSubDirectory"></param>
        /// <param name="fileFilter"></param>
        /// <returns></returns>
        public static FileInfo[] GetFiles( string directPath, bool containSubDirectory, string fileFilter )
        {
            DirectoryInfo directInfo = new DirectoryInfo( directPath );
            if ( !directInfo.Exists )
            {
                return new FileInfo[ 0 ];
            }
            SearchOption option = ( containSubDirectory ? SearchOption.AllDirectories : SearchOption.TopDirectoryOnly );
            if ( String.IsNullOrEmpty( fileFilter ) )
            {
                fileFilter = "*.*";
            }
            return directInfo.GetFiles( fileFilter, option );
        }

        /// <summary>
        /// 写文本到文件中
        /// </summary>
        /// <param name="path"></param>
        /// <param name="txt"></param>
        /// <param name="append"></param>
        public static void WriteTextToFile( string path, string txt, bool append )
        {
            WriteTextToFile( path, txt, Encoding.Default, append );
        }

        /// <summary>
        /// 写文本到文件中
        /// </summary>
        /// <param name="path"></param>
        /// <param name="txt"></param>
        /// <param name="encoding"></param>
        /// <param name="append"></param>
        public static void WriteTextToFile( string path, string txt, Encoding encoding, bool append )
        {
            StreamWriter stream = new StreamWriter( path, append, encoding );
            stream.Write( txt );
            stream.Close();
        }

        /// <summary>
        /// 写文件
        /// </summary>
        /// <param name="path"></param>
        /// <param name="data"></param>
        public static void Write( string path, byte[] data )
        {
            string directoryPath = Path.GetDirectoryName( path );
            if ( !Directory.Exists( directoryPath ) )
            {
                Directory.CreateDirectory( directoryPath );
            }
            FileStream stream = new FileStream( path, FileMode.Create, FileAccess.Write, FileShare.ReadWrite );
            stream.Write( data, 0, data.Length );
            stream.Close();
        }

        /// <summary>
        /// 读文件到二进制数组
        /// </summary>
        /// <param name="path"></param>
        /// <returns></returns>
        public static byte[] Read( string path )
        {
            string directoryPath = Path.GetDirectoryName( path );
            if ( !Directory.Exists( directoryPath ) )
            {
                Directory.CreateDirectory( directoryPath );
            }
            FileStream stream = new FileStream( path, FileMode.OpenOrCreate, FileAccess.Read, FileShare.ReadWrite );
            byte[] data = new byte[ stream.Length ];
            stream.Read( data, 0, ( int )stream.Length );
            stream.Close();
            return data;
        }

        /// <summary>
        /// 获取指定目录下的文件数
        /// </summary>
        /// <param name="dir"></param>
        /// <returns></returns>
        public static int GetFileCount( DirectoryInfo dir )
        {
            return GetFileCount( dir, true );
        }

        /// <summary>
        /// 获取文件数目
        /// </summary>
        /// <param name="dir"></param>
        /// <param name="includeSub"></param>
        /// <returns></returns>
        public static int GetFileCount( DirectoryInfo dir, bool includeSub )
        {
            int files = dir.GetFiles().Length;
            if ( includeSub )
                foreach ( DirectoryInfo subDir in dir.GetDirectories() )
                    files += GetFileCount( subDir, true );
            return files;
        }

        /// <summary>
        /// 将文件内容读入到字符串
        /// </summary>
        /// <param name="path">文件路径，至少包括文件名</param>
        /// <returns></returns>
        public static string ReadFile2String( string path )
        {
            string value = String.Empty;
            if ( !String.IsNullOrEmpty( path ) )
            {
                if ( !Path.IsPathRooted( path ) )
                    path = Path.Combine( currentPath, path );
                if ( File.Exists( path ) )
                    using ( StreamReader reader = new StreamReader( path ) )
                    {
                        value = reader.ReadToEnd();
                    }
            }
            return value;
        }

        /// <summary>
        /// 将字符串内容写入到文件
        /// </summary>
        /// <param name="Content"></param>
        /// <param name="Path"></param>
        public static void WriteString2File( string Content, string Path )
        {
            using ( StreamWriter wrtier = new StreamWriter( Path ) )
            {
                wrtier.Write( Content );
                wrtier.Flush();
            }
        }

        /// <summary>
        /// 创建文件夹
        /// </summary>
        /// <param name="path">文件夹的路径</param>
        /// <returns>创建成功 返回 true</returns>
        public static bool CreateDirectory( string path )
        {
            if ( String.IsNullOrEmpty( path ) )
                return false;

            try
            {
                if ( !Directory.Exists( path ) )
                {
                    Directory.CreateDirectory( path );
                }
                return true;
            }
            catch ( Exception ex )
            {
                Logger.LogError( ex, "Unable to Create Directory: ", path);
            }
            return false;
        }

        /// <summary>
        /// 复制文件
        /// </summary>
        /// <param name="sourcePath">源路径</param>
        /// <param name="targetPath">目标路径</param>
        /// <param name="fileName">文件名</param>
        /// <returns>是否成功</returns>
        public static bool CopyFileWithRetry(string sourcePath, string targetPath, string fileName)
        {
            return CopyFileWithRetry(Path.Combine(sourcePath, fileName), Path.Combine(targetPath, fileName));
        }

        /// <summary>
        /// 复制文件
        /// </summary>
        /// <param name="sourceFile">源文件</param>
        /// <param name="destinationFile">目标文件</param>
        /// <returns>是否复制成功</returns>
        /// <remarks></remarks>
        public static bool CopyFileWithRetry(string sourceFile, string destinationFile)
        {
            return CopyFileWithRetry(sourceFile, destinationFile, false);
        }

        /// <summary>
        /// 复制文件，带有重试机制
        /// </summary>
        /// <param name="srcFile"></param>
        /// <param name="destFile"></param>
        /// <param name="autoCreateDir"></param>
        /// <returns></returns>
        public static bool CopyFileWithRetry(string srcFile, string destFile, bool autoCreateDir)
        {
            bool result = false;
            for (int retry = 1; retry <= Constants.FileCopyMaxRetryTime; retry++)
            {
                result = CopyFile(srcFile, destFile, autoCreateDir);
                if (result)
                {
                    break;
                }
                else
                {
                    Logger.LogInfo("复制文件", srcFile, "至", destFile, "时发生错误。");
                    if (retry != Constants.FileCopyMaxRetryTime)
                    {
                        Logger.LogInfo("将尝试重新复制。");
                        Thread.Sleep(Constants.FileCopyRetryInterval);
                    }
                }
            }
            return result;
        }

        /// <summary>
        /// 复制文件
        /// </summary>
        /// <param name="sourceFile">源文件</param>
        /// <param name="destinationFile">目标文件</param>
        /// <param name="autoCreateDir">是否自动建立目标文件夹</param>
        /// <returns>是否复制成功</returns>
        public static bool CopyFile( string sourceFile, string destinationFile, bool autoCreateDir )
        {
            bool CopyFile;
            try
            {
                if ( !File.Exists( sourceFile ) )
                {
                    return false;
                }
                if ( File.Exists( destinationFile ) )
                {
                    File.SetAttributes( destinationFile, FileAttributes.Normal );
                }

                // 自动建立目标文件夹
                if ( autoCreateDir )
                {
                    string desDirName = Path.GetDirectoryName( destinationFile );
                    if ( !Directory.Exists( desDirName ) )
                    {
                        Directory.CreateDirectory( desDirName );
                    }
                }

                File.Copy( sourceFile, destinationFile, true );
                CopyFile = true;
            }
            catch ( Exception ex )
            {
                Logger.LogError( ex, "Unable to Copy file: ", sourceFile);
                CopyFile = false;
                return CopyFile;
            }
            return CopyFile;
        }

        /// <summary>
        /// Replace the ipaddress in url
        /// </summary>
        /// <param name="Url"></param>
        /// <param name="IpAddress"></param>
        /// <returns></returns>
        public static string ReplaceUrlIp(string Url, string IpAddress)
        {
            string tempUrl = Url.Substring(Url.IndexOf("//", StringComparison.InvariantCultureIgnoreCase) + 2);
            tempUrl = tempUrl.Substring(tempUrl.IndexOf("/", StringComparison.InvariantCultureIgnoreCase) + 1);
            return "http://" + IpAddress + "/" + tempUrl;
        }

        /// <summary>
        /// Deletes file with retry.
        /// </summary>
        public static bool DeleteFileWithRetry(string filePath)
        {
            bool result = false;
            for (int retry = 1; retry <= Constants.FileCopyMaxRetryTime; retry++)
            {
                try
                {
                    File.Delete(filePath);
                    Logger.LogInfo("删除文件", filePath, "成功。");
                    result = true;
                }
                catch (Exception ex)
                {
                    Logger.LogError(ex);
                    result = false;
                }

                if (result)
                {
                    break;
                }
                else
                {
                    Logger.LogInfo("删除文件", filePath, "时发生错误。");
                    if (retry != Constants.FileCopyMaxRetryTime)
                    {
                        Logger.LogInfo("将尝试重新删除。");
                        Thread.Sleep(Constants.FileCopyRetryInterval);
                    }
                }
            }
            return result;
        }
    }
}
