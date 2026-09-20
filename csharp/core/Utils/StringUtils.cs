using System;

namespace AlibabaCloud.TeaXML.Utils
{
    internal static class StringUtils
    {
        internal static int SubStringCount(string str, string subString)
        {
            if (subString.Length < 1)
            {
                //if subString is Empty, let's throw an ArgumentException similar to `String.Replace()`.
                throw new ArgumentException("The length of subString cannot be zero.");
            }
            int count = 0, index = str.IndexOf(subString, StringComparison.Ordinal);
            while (index >= 0)
            {
                count++;
                index = str.IndexOf(subString, index + subString.Length, StringComparison.Ordinal);
            }
            return count;
        }
    }
}