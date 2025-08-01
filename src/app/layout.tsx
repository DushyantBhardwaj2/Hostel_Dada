import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'
import { FirebaseProvider } from '@/lib/firebase-context'
import { Toaster } from '@/components/ui/toaster'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Hostel Dada - Smart Hostel Management',
  description: 'Complete hostel management solution with food ordering, roommate matching, laundry booking, mess management, and maintenance tracking',
  keywords: ['hostel', 'management', 'student', 'accommodation', 'food ordering', 'roommate'],
  authors: [{ name: 'Hostel Dada Team' }],
  viewport: 'width=device-width, initial-scale=1',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={inter.className}>
        <FirebaseProvider>
          {children}
          <Toaster />
        </FirebaseProvider>
      </body>
    </html>
  )
}
