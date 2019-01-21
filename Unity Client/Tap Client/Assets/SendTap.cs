using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

public class SendTap : MonoBehaviour {

    [SerializeField]
    Text text;

	public void SendTapFunction () {
        HttpWebRequest request = (HttpWebRequest)WebRequest.Create("http://104.248.203.116:2222/increment");
        HttpWebResponse response = (HttpWebResponse)request.GetResponse();
        if (response.StatusCode == HttpStatusCode.Forbidden)
        {
            Debug.LogError("Нельзя обработать ответ (404)");
        }
        else if (response.StatusCode == HttpStatusCode.OK)
        {
            Debug.LogError("Страница загружена");
        }

        Stream stream = response.GetResponseStream();
        StreamReader sr = new StreamReader(stream, Encoding.GetEncoding(response.CharacterSet));
        string sReadData = sr.ReadToEnd();
        Debug.LogError(sReadData);

        response.Close();
    }

    StreamReader reader;

    private void Start()
    {
        WebClient client = new WebClient();
        Stream data = client.OpenRead("http://104.248.203.116:2222/stream");
        reader = new StreamReader(data);
    }

    private void Update()
    {
        if (!reader.EndOfStream)
        {
            string s = reader.ReadLine();
            text.text = s;
        }
    }


}
