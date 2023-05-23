﻿using MQTTnet;
using MQTTnet.Server;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace HamsterIoHTests
{
    [TestFixture]
    public class CertificateTests : StateTests
    {
        protected X509Certificate2 GetServerCertificate()
        {
            var dir = Path.GetFullPath(Environment.CurrentDirectory);
            while (!Directory.Exists(Path.Combine(dir!, "certs")))
            {
                dir = Path.GetDirectoryName(dir);
            }

            return new X509Certificate2( Path.Combine(dir, "certs", "server.pfx") );
        }

        protected override string? CreateClientOptions()
        {
            return "-e";
        }

        X509Certificate2 CompensateWindowsEpheralKeysProblem(X509Certificate2 cert)
        {
            if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
            {
                // SSLStream on Windows throws with ephemeral key sets
                // workaround from https://github.com/dotnet/runtime/issues/23749#issuecomment-388231655
                var originalCert = cert;
                cert = new X509Certificate2(cert.Export(X509ContentType.Pkcs12));
                originalCert.Dispose();
            }
            return cert;
        }

        protected override MqttServerOptions CreateServerOptions()
        {
            return MqttFactory.CreateServerOptionsBuilder()
                .WithEncryptedEndpoint()
                .WithEncryptedEndpointBoundIPAddress(IPAddress.Loopback)
                .WithEncryptionCertificate(GetServerCertificate())
                .WithConnectionBacklog(50)
                .Build();
        }
    }
}
